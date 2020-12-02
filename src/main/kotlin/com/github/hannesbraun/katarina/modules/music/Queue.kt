package com.github.hannesbraun.katarina.modules.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Queue {
    private val list = mutableListOf<AudioTrack>()
    private val mutex = Mutex()

    suspend fun shuffle() = mutex.withLock { list.shuffle() }
    suspend fun isEmpty() = mutex.withLock { list.isEmpty() }
    suspend fun clear() = mutex.withLock { list.clear() }
    suspend fun add(track: AudioTrack) = mutex.withLock { list.add(track) }
    suspend fun removeFirst() = mutex.withLock { list.removeFirst() }

    suspend fun limit(toIndex: Int): MutableList<AudioTrack> {
        return mutex.withLock {
            val sub = if (list.size > 21) list.subList(0, 21) else list
            val limited = mutableListOf<AudioTrack>()
            for (track in sub) {
                limited.add(track)
            }
            limited
        }
    }
}