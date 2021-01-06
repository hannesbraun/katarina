package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.KatarinaGuildOnlyException
import com.github.hannesbraun.katarina.utilities.KatarinaUnconnectedException
import com.github.hannesbraun.katarina.utilities.KatarinaWrongChannelException
import com.github.hannesbraun.katarina.utilities.deleteAfter
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.requests.restaction.MessageAction
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

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

    /* Mutex for opening and closing connections */
    private val connectionMutex = Mutex()

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
        scope.launch {
            val player = connectAndGetPlayer(event, channel)
            val scheduler = schedulers[player]
            if (scheduler == null) {
                connectionMutex.withLock { event.guild.audioManager.closeAudioConnection() }
                player.destroy()
                LoggerFactory.getLogger("MusicBot").error("No track scheduler found for this player")
                return@launch
            }
            for (url in command.urls) {
                playerManager.loadItem(url, LoadResultHandler(scheduler, event.textChannel, scheduler.scope))
            }
        }
    }

    private suspend fun connectAndGetPlayer(event: MessageReceivedEvent, channel: VoiceChannel): AudioPlayer {
        val audioManager = event.guild.audioManager
        return connectionMutex.withLock {
            if (!audioManager.isConnected) {
                // Connect to voice channel
                audioManager.openAudioConnection(channel)

                // Create player
                val player = playerManager.createPlayer()
                val scheduler = TrackScheduler(player, event.textChannel, CoroutineScope(Dispatchers.Unconfined)) {
                    disconnector(audioManager, player)
                }
                player.addListener(scheduler)
                schedulers[player] = scheduler

                // Set sending handler for JDA
                val sendingHandler = AudioPlayerSendHandler(player)
                audioManager.sendingHandler = sendingHandler
                player
            } else {
                // Connection already established
                // If the bot will be disconnected during the following statements, the command will simply be ignored
                // -> This is fine
                val sendingHandler = audioManager.sendingHandler
                if (sendingHandler is AudioPlayerSendHandler) {
                    sendingHandler.audioPlayer
                } else {
                    throw RuntimeException("Sending handler is not an instance of AudioPlayerSendHandler")
                }
            }
        }
    }

    private fun stop(event: MessageReceivedEvent) {
        checkSameChannel(event)
        disconnector(event.guild.audioManager, getPlayer(event))
    }

    private fun disconnector(audioManager: AudioManager, player: AudioPlayer?) {
        GlobalScope.launch {
            connectionMutex.withLock {
                audioManager.closeAudioConnection()
                audioManager.sendingHandler = null
                schedulers[player]?.scope?.cancel()
                player?.destroy()
                schedulers.remove(player)
            }
        }
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

    /* Checks if the bot and the user are connected to the same channel. The voice channel the user is connected to will be returned.
    * No need to get worried about concurrency here: if the user disconnects directly after executing the command, that's how it is.
    * Whatever... the only thing that is important is the connection state of the bot. But this will be checked again when actually executing something.
    */
    private fun checkSameChannel(event: MessageReceivedEvent, allowUnconnectedBot: Boolean = false): VoiceChannel {
        val userChannel =
            event.member?.voiceState?.channel
                ?: throw KatarinaUnconnectedException("You are not connected to a voice channel.")
        if (!allowUnconnectedBot && !event.guild.audioManager.isConnected) {
            throw KatarinaUnconnectedException("${config.botName} is not connected to a voice channel")
        } else if (event.guild.audioManager.isConnected && event.guild.audioManager.connectedChannel?.id != userChannel.id) {
            throw KatarinaWrongChannelException("This command can only be executed while being connected in the same voice channel as ${config.botName}.")
        }
        return userChannel
    }

    private fun getPlayer(event: MessageReceivedEvent): AudioPlayer =
        (event.guild.audioManager.sendingHandler as AudioPlayerSendHandler).audioPlayer
}
