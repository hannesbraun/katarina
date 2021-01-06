package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.utilities.deleteAfter
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.TextChannel

class LoadResultHandler(
    private val scheduler: TrackScheduler,
    private val textChannel: TextChannel,
    private val scope: CoroutineScope
) :
    AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack?) {
        if (track != null)
            scope.launch { scheduler.queue(track) }
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        if (playlist != null) {
            scope.launch {
                for (track in playlist.tracks) {
                    scheduler.queue(track, true)
                }
                scheduler.sendQueuePlaylistSuccess(playlist.tracks.size)
            }
        }
    }

    override fun noMatches() {
        textChannel.sendMessage("Nothing matches your request. I'm sorry.")
            .deleteAfter(MessageDeletionTimes.medium)
            .queue()
        scheduler.onLoadFailed()
    }

    override fun loadFailed(exception: FriendlyException?) {
        if (exception?.severity == FriendlyException.Severity.COMMON) {
            textChannel.sendMessage("Unable to load the requested track: ${exception.message}")
                .deleteAfter(MessageDeletionTimes.medium)
                .queue()
        }
        scheduler.onLoadFailed()
    }
}
