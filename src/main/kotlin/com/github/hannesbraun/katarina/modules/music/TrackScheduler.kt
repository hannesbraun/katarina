package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.dv8tion.jda.api.entities.TextChannel
import org.apache.commons.text.StringEscapeUtils
import java.util.*
import java.util.concurrent.TimeUnit

class TrackScheduler(
    private val player: AudioPlayer,
    private val textChannel: TextChannel,
    val scope: CoroutineScope,
    private val disconnector: () -> Unit
) : AudioEventAdapter() {
    private val queue = Queue()
    private var running = false
    private val queueMutex = Mutex()

    init {
        scope.launch { monitorUnused() }
    }

    private val sourcesWithAuthor = listOf(
        "bandcamp",
        "twitch"
    )

    override fun onPlayerPause(player: AudioPlayer?) = Unit

    override fun onPlayerResume(player: AudioPlayer?) = Unit

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        if (track == null)
            return

        val npString = "**Now playing**: ${getTrackDisplayName(track)}"

        textChannel.sendMessage(npString)
            .deleteAfter(track.duration, TimeUnit.MILLISECONDS)
            .queue()
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        when (endReason) {
            AudioTrackEndReason.CLEANUP -> disconnector()
            AudioTrackEndReason.FINISHED -> playNext()
            AudioTrackEndReason.LOAD_FAILED -> {
                textChannel.sendMessage("Loading ${track?.info?.title} failed.")
                    .deleteAfter(MessageDeletionTimes.medium)
                    .queue()
                playNext()
            }
            AudioTrackEndReason.REPLACED -> Unit // No need to do something here since replacing or stopping was done
            AudioTrackEndReason.STOPPED -> Unit // on purpose somewhere else in this class
        }
    }

    private fun playNext() {
        scope.launch {
            if (queue.isEmpty()) {
                disconnector()
            } else {
                // Play next track
                player.playTrack(queue.removeFirst())
            }
        }
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        textChannel.sendMessage("Exception while playing ${track?.info?.title}. Skipping this track...")
            .deleteAfter(MessageDeletionTimes.long)
            .queue()
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        textChannel.sendMessage("The track ${track?.info?.title} is stuck. Skipping this track...")
            .deleteAfter(MessageDeletionTimes.medium)
            .queue()
        playNext()
    }

    fun onLoadFailed() {
        if (!running) {
            disconnector()
        }
    }

    private fun getTrackDisplayName(track: AudioTrack): String {
        val name = if (track.sourceManager.sourceName in sourcesWithAuthor)
            "${track.info.author} - ${track.info.title}"
        else
            track.info.title

        return if (track.sourceManager.sourceName == "bandcamp") {
            StringEscapeUtils.unescapeHtml4(name)
        } else name
    }

    suspend fun queue(track: AudioTrack, playlist: Boolean = false) {
        queueMutex.withLock {
            if (running) {
                queue.add(track)
                val trackName = getTrackDisplayName(track)
                if (!playlist) {
                    textChannel.sendMessage("Successfully added \"$trackName\" to the queue.")
                        .deleteAfter(MessageDeletionTimes.short)
                        .queue()
                }
            } else {
                running = true
                player.playTrack(track)
            }
        }
    }

    fun sendQueuePlaylistSuccess(amount: Int) {
        textChannel.sendMessage("Successfully added $amount ${if (amount == 1) "track" else "tracks"} to the queue.")
            .deleteAfter(MessageDeletionTimes.short)
            .queue()
    }

    fun pause() {
        player.isPaused = !player.isPaused
    }

    private suspend fun monitorUnused() {
        var pausedCounter = 0
        while (pausedCounter <= TimeUnit.HOURS.toMinutes(12)) {
            delay(60000)
            if (player.isPaused)
                pausedCounter++
            else
                pausedCounter = 0
        }
        // At least paused for 12 hours
        // Improbable: player always only ran between the "paused samples"
        // So shut down this player and do whatever needs to be done with the disconnector
        disconnector()
    }

    fun clearQueue() = scope.launch { queue.clear() }

    fun skip() = playNext()

    fun sendQueue(textChannel: TextChannel) {
        scope.launch {
            if (queue.isEmpty()) {
                textChannel.sendMessage("The queue is empty.")
                    .deleteAfter(MessageDeletionTimes.medium)
                    .queue()
                return@launch
            }

            val limitedQueue = queue.limit(21)
            val trackStrings = mutableListOf<String>()
            for ((index, track) in limitedQueue.withIndex()) {
                val trackString: String = if (track.sourceManager.sourceName in sourcesWithAuthor)
                    "**${index + 1}.** ${track.info.author} - ${track.info.title}"
                else
                    "**${index + 1}.** ${track.info.title}"
                trackStrings.add(trackString.limitWithDots(80))
            }
            textChannel.sendMessage(trackStrings.joinToString("\n").limit(2000))
                .deleteAfter(MessageDeletionTimes.long)
                .queue()
        }
    }

    fun shuffle() = scope.launch { queue.shuffle() }

    fun finalize() {
        scope.cancel()
    }
}
