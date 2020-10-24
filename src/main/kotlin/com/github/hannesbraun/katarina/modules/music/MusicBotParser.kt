package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.modules.KatarinaParser
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class MusicBotParser : KatarinaParser() {
    val baseCommandMap = mapOf<String, MusicBotBaseCommand>("play" to MusicBotBaseCommand.PLAY,
            "pause" to MusicBotBaseCommand.PAUSE,
            "stop" to MusicBotBaseCommand.STOP,
            "clearqueue" to MusicBotBaseCommand.CLEARQUEUE,
            "skip" to MusicBotBaseCommand.SKIP,
            "queue" to MusicBotBaseCommand.QUEUE,
            "shuffle" to MusicBotBaseCommand.SHUFFLE)

    fun canHandle(event: MessageReceivedEvent): Boolean {
        val msg = event.message.contentRaw
        if (!super.canHandle(msg)) return false
        return true
    }

    fun getCommand(event: MessageReceivedEvent): MusicBotCommand {
        val msg = event.message.contentRaw
        val args = splitArgs(msg)
        val baseCommand : MusicBotBaseCommand = baseCommandMap[args[0].toLowerCase()] ?: MusicBotBaseCommand.NONE

        if (baseCommand == MusicBotBaseCommand.PLAY && args.size < 2) {
            throw IllegalArgumentException("URL for play command is missing")
        }

        return MusicBotCommand(baseCommand, args)
    }
}

enum class MusicBotBaseCommand {
    PLAY,
    PAUSE,
    STOP,
    CLEARQUEUE,
    SKIP,
    QUEUE,
    SHUFFLE,
    NONE
}

data class MusicBotCommand(val baseCommand: MusicBotBaseCommand, val args: List<String>)