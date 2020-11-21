package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.managers.AudioManager
import java.util.*
import java.util.concurrent.TimeUnit

class TrackScheduler(private val player: AudioPlayer, private val textChannel: TextChannel, private val disconnector: () -> Unit) : AudioEventAdapter() {
    private val queue = Collections.synchronizedList(mutableListOf<AudioTrack>())
    private var running = false

    override fun onPlayerPause(player: AudioPlayer?) {
    }

    override fun onPlayerResume(player: AudioPlayer?) {
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        if (track == null)
            return

        textChannel.sendMessage("**Now playing**: ${track.info.author} - ${track.info.title}")
            .delay(track.duration, TimeUnit.MILLISECONDS)
            .flatMap{it.delete()}
            .queue()
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        when (endReason) {
            AudioTrackEndReason.CLEANUP -> disconnector()
            AudioTrackEndReason.FINISHED -> playNext()
            AudioTrackEndReason.LOAD_FAILED -> {
                textChannel.sendMessage("Loading ${track?.info?.title} failed.")
                    .delay(10, TimeUnit.MINUTES)
                    .flatMap{it.delete()}
                    .queue()
                playNext()
            }
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
        textChannel.sendMessage("${track?.info?.title} is stuck. Skipping this track...")
            .delay(24, TimeUnit.HOURS)
            .flatMap{it.delete()}
            .queue()
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        textChannel.sendMessage("The track ${track?.info?.title} is stuck. Skipping this track...")
            .delay(10, TimeUnit.MINUTES)
            .flatMap{it.delete()}
            .queue()
        playNext()
    }

    @Synchronized
    fun queue(track: AudioTrack) {
        if (running) {
            queue.add(track)
            textChannel.sendMessage("Successfully added to queue.")
                .delay(30, TimeUnit.SECONDS)
                .flatMap{it.delete()}
                .queue()
        } else {
            running = true
            player.playTrack(track)
        }
    }

    fun pause() = player.setPaused(!player.isPaused)

    fun clearQueue() = queue.clear()

    fun skip() = playNext()

    fun sendQueue(textChannel : TextChannel) {
        val limitedQueue = if (queue.size > 21) queue.subList(0,21) else queue
        var trackStrings = mutableListOf<String>()
        for ((index, track) in limitedQueue.withIndex()) {
            trackStrings.add("**${index+1}.** ${track.info.author} - ${track.info.title}".limitWithDots(80))
        }
        textChannel.sendMessage(trackStrings.joinToString("\n").limit(2000))
            .delay(7, TimeUnit.DAYS)
            .flatMap {it.delete()}
            .queue()
    }

    fun shuffle() = queue.shuffle()
}
