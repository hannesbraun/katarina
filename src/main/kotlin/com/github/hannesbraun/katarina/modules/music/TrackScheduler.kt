package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*
import java.util.concurrent.TimeUnit

class TrackScheduler(
    private val player: AudioPlayer,
    private val textChannel: TextChannel,
    private val disconnector: () -> Unit
) : AudioEventAdapter() {
    private val queue = Collections.synchronizedList(mutableListOf<AudioTrack>())
    private var running = false

    private val sourcesWithAuthor = listOf(
        "bandcamp",
        "twitch"
    )

    override fun onPlayerPause(player: AudioPlayer?) {
    }

    override fun onPlayerResume(player: AudioPlayer?) {
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        if (track == null)
            return

        val npString: String = if (track.sourceManager.sourceName in sourcesWithAuthor)
            "**Now playing**: ${track.info.author} - ${track.info.title}"
        else
            "**Now playing**: ${track.info.title}"

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
        if (queue.isEmpty()) {
            disconnector()
        } else {
            // Play next track
            player.playTrack(queue.removeFirst())
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

    @Synchronized
    fun queue(track: AudioTrack) {
        if (running) {
            queue.add(track)
            textChannel.sendMessage("Successfully added to queue.")
                .deleteAfter(MessageDeletionTimes.short)
                .queue()
        } else {
            running = true
            player.playTrack(track)
        }
    }

    fun pause() = player.setPaused(!player.isPaused)

    fun clearQueue() = queue.clear()

    fun skip() = playNext()

    fun sendQueue(textChannel: TextChannel) {
        if (queue.isEmpty()) {
            textChannel.sendMessage("The queue is empty.")
                .deleteAfter(MessageDeletionTimes.medium)
                .queue()
            return
        }

        val limitedQueue = if (queue.size > 21) queue.subList(0, 21) else queue
        var trackStrings = mutableListOf<String>()
        for ((index, track) in limitedQueue.withIndex()) {
            val npString: String = if (track.sourceManager.sourceName in sourcesWithAuthor)
                "**${index + 1}.** ${track.info.author} - ${track.info.title}"
            else
                "**${index + 1}.** ${track.info.title}"
            trackStrings.add(npString.limitWithDots(80))
        }
        textChannel.sendMessage(trackStrings.joinToString("\n").limit(2000))
            .deleteAfter(MessageDeletionTimes.long)
            .queue()
    }

    fun shuffle() = queue.shuffle()
}
