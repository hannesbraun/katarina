package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.KatarinaGuildOnlyException
import com.github.hannesbraun.katarina.utilities.KatarinaUnconnectedException
import com.github.hannesbraun.katarina.utilities.KatarinaWrongChannelException
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object MessageDeletionTimes {
    /* Time in minutes until a music bot related message will be deleted*/
    const val short = 1L
    const val medium = 5L
    const val long = 1440L
}

class MusicBot(private val scope: CoroutineScope, private val config: KatarinaConfiguration) : KatarinaModule(),
    MessageReceivedHandler {
    private val parser = MusicBotParser(config)

    private val playerManager = DefaultAudioPlayerManager()

    private val schedulers = mutableMapOf<AudioPlayer, TrackScheduler>()

    /* Lock for opening and closing connections */
    private val connectionLock = ReentrantLock()

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
    }

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val command = parser.parse(event.message.contentRaw) ?: return false
        if (!event.isFromGuild) throw KatarinaGuildOnlyException("${command.baseCommand.rawCommand} is only allowed on a server")

        when (command.baseCommand) {
            MusicBotBaseCommand.PLAY -> play(event, command)
            MusicBotBaseCommand.PAUSE -> pause(event)
            MusicBotBaseCommand.STOP -> stop(event)
            MusicBotBaseCommand.CLEAR_QUEUE -> clearQueue(event)
            MusicBotBaseCommand.SKIP -> skip(event)
            MusicBotBaseCommand.QUEUE -> sendQueue(event)
            MusicBotBaseCommand.SHUFFLE -> shuffle(event)
        }

        if (command.baseCommand != MusicBotBaseCommand.QUEUE)
            event.message.deleteAfter(MessageDeletionTimes.short)
        else
            event.message.deleteAfter(MessageDeletionTimes.long)
        return true
    }

    private fun play(event: MessageReceivedEvent, command: MusicBotCommand) {
        val channel = checkSameChannel(event, true)
        val player = connectAndGetPlayer(event, channel)
        val scheduler = schedulers[player]
        if (scheduler == null) {
            connectionLock.withLock { event.guild.audioManager.closeAudioConnection() }
            player.destroy()
            throw RuntimeException("No track scheduler found for this player")
        }
        for (url in command.urls) {
            playerManager.loadItem(url, LoadResultHandler(scheduler, event.textChannel))
        }
    }

    @Synchronized
    private fun connectAndGetPlayer(event: MessageReceivedEvent, channel: VoiceChannel): AudioPlayer {
        val audioManager = event.guild.audioManager
        return if (!audioManager.isConnected) {
            // Connect to voice channel
            connectionLock.withLock { audioManager.openAudioConnection(channel) }

            // Create player
            val player = playerManager.createPlayer()
            val scheduler = TrackScheduler(player, event.textChannel) {
                connectionLock.withLock { audioManager.closeAudioConnection() }
                player.destroy()
            }
            player.addListener(scheduler)
            schedulers[player] = scheduler

            // Set sending handler for JDA
            val sendingHandler = AudioPlayerSendHandler(player)
            audioManager.sendingHandler = sendingHandler
            player
        } else {
            // Connection already established
            val sendingHandler = audioManager.sendingHandler
            if (sendingHandler is AudioPlayerSendHandler) {
                sendingHandler.audioPlayer
            } else {
                throw RuntimeException("Sending handler is not an instance of AudioPlayerSendHandler")
            }
        }
    }

    private fun stop(event: MessageReceivedEvent) {
        checkSameChannel(event)
        connectionLock.withLock { event.guild.audioManager.closeAudioConnection() }
        getPlayer(event)?.destroy()
    }

    private fun pause(event: MessageReceivedEvent) {
        checkSameChannel(event)
        schedulers[getPlayer(event)]?.pause()
    }

    private fun clearQueue(event: MessageReceivedEvent) {
        checkSameChannel(event)
        schedulers[getPlayer(event)]?.clearQueue()
    }

    private fun skip(event: MessageReceivedEvent) {
        checkSameChannel(event)
        schedulers[getPlayer(event)]?.skip()
    }

    private fun sendQueue(event: MessageReceivedEvent) {
        checkSameChannel(event)
        schedulers[getPlayer(event)]?.sendQueue(event.textChannel)
    }

    private fun shuffle(event: MessageReceivedEvent) {
        checkSameChannel(event)
        schedulers[getPlayer(event)]?.shuffle()
    }

    /* Checks if the bot and the user are connected to the same channel. The voice channel the user is connected to will be returned. */
    private fun checkSameChannel(event: MessageReceivedEvent, allowUnconnectedBot: Boolean = false): VoiceChannel {
        val userChannel =
            event.member?.voiceState?.channel ?: throw KatarinaUnconnectedException("Not connected to a voice channel")
        if (!allowUnconnectedBot && !event.guild.audioManager.isConnected) {
            throw KatarinaUnconnectedException("Katarina is not connected to a voice channel")
        } else if (event.guild.audioManager.isConnected && event.guild.audioManager.connectedChannel?.id != userChannel.id) {
            throw KatarinaWrongChannelException("This command can only be executed while being connected in the same voice channel as Katarina.")
        }
        return userChannel
    }

    private fun getPlayer(event: MessageReceivedEvent): AudioPlayer? =
        (event.guild.audioManager.sendingHandler as AudioPlayerSendHandler).audioPlayer
}

fun Message.deleteAfter(minutes: Long) {
    this.delete().queueAfter(minutes, TimeUnit.MINUTES)
}

fun MessageAction.deleteAfter(minutes: Long) =
    this.delay(minutes, TimeUnit.MINUTES)
        .flatMap { it.delete() }


fun MessageAction.deleteAfter(duration: Long, unit: TimeUnit) =
    this.delay(duration, unit)
        .flatMap { it.delete() }
