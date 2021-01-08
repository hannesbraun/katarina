package com.github.hannesbraun.katarina.modules.music.monstercat

import com.github.hannesbraun.katarina.modules.danbooru.getString
import com.google.gson.JsonParser
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack
import com.sedmelluq.discord.lavaplayer.track.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.io.DataInput
import java.io.DataOutput

class MonstercatSourceManager(private val scope: CoroutineScope) : AudioSourceManager {
    private val httpAudioSourceManager = HttpAudioSourceManager()
    override fun getSourceName(): String = "monstercat"

    override fun loadItem(manager: DefaultAudioPlayerManager?, reference: AudioReference?): AudioItem? {
        if (reference == null) return null

        // Parse catalogId
        if (!reference.identifier.startsWith("https://www.monstercat.com/release/")) return null
        var catalogId = reference.identifier.removePrefix("https://www.monstercat.com/release/")
        val endIndex = catalogId.indexOfFirst { !(it.isLetterOrDigit() || it == '-') }
        if (endIndex != -1) {
            catalogId = catalogId.substring(0, endIndex)
        }

        // Get metadata JSON
        val deferredJson = scope.async {
            HttpClient(CIO).use { client ->
                client.get<String>("https://connect.monstercat.com/v2/catalog/release/$catalogId")
            }
        }

        // TODO: this is very ugly
        while (deferredJson.isActive) {
            Thread.sleep(100)
        }
        val json = try {
            JsonParser.parseString(deferredJson.getCompleted()).asJsonObject
        } catch (e: IllegalStateException) {
            return null
        }

        // Extract some release information
        val releaseId = json.getAsJsonObject("release").getString("id")
        val releaseTitle = json.getAsJsonObject("release").getString("title")

        fun mapToTrack(reference: AudioReference, ttl: Int = 16): HttpAudioTrack? {
            if (ttl <= 0) return null // Prevent endless redirections

            return when (val item = httpAudioSourceManager.loadItem(manager, reference)) {
                null -> null
                is HttpAudioTrack -> item
                is AudioReference -> mapToTrack(item, ttl - 1) // Redirect
                else -> null
            }
        }

        val tracks = json.getAsJsonArray("tracks").map {
            mapToTrack(
                AudioReference(
                    "https://connect.monstercat.com/v2/release/$releaseId/track-stream/${
                        it.asJsonObject.getString("id")
                    }",
                    reference.title
                )
            ) ?: return null
        }

        return when (tracks.size) {
            0 -> null // No tracks found
            1 -> tracks.first()
            else -> BasicAudioPlaylist(releaseTitle, tracks, tracks.first(), false)
        }
    }

    override fun isTrackEncodable(track: AudioTrack?): Boolean = httpAudioSourceManager.isTrackEncodable(track)

    override fun encodeTrack(track: AudioTrack?, output: DataOutput?)  = httpAudioSourceManager.encodeTrack(track, output)

    override fun decodeTrack(trackInfo: AudioTrackInfo?, input: DataInput?): AudioTrack = httpAudioSourceManager.decodeTrack(trackInfo, input)

    override fun shutdown() = httpAudioSourceManager.shutdown()
}
