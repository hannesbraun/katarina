package com.github.hannesbraun.katarina.modules.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

class LoadResultHandler(private val scheduler: TrackScheduler, private val textChannel: TextChannel) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack?) {
        if (track != null)
            scheduler.queue(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        if (playlist != null) {
            for (track in playlist.tracks) {
                scheduler.queue(track)
            }
        }
    }

    override fun noMatches() {
        textChannel.sendMessage("Nothing matches your request. I'm sorry.")
            .delay(10, TimeUnit.MINUTES)
            .flatMap { it.delete() }
            .queue()
    }

    override fun loadFailed(exception: FriendlyException?) {
        if (exception?.severity == FriendlyException.Severity.COMMON) {
            textChannel.sendMessage("Unable to load the requested track: ${exception.message}")
                .delay(10, TimeUnit.MINUTES)
                .flatMap { it.delete() }
                .queue()
        }
    }
}