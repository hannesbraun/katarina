package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.KatarinaParsingException

class MusicBotParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): MusicBotCommand? {
        val args = splitArgs(message)
        val baseCommand = MusicBotBaseCommand.fromString(args[0].toLowerCase()) ?: return null

        return if (baseCommand == MusicBotBaseCommand.PLAY && args.size < 2) {
            throw KatarinaParsingException("URL for play command is missing")
        } else if (baseCommand == MusicBotBaseCommand.PLAY) {
            MusicBotCommand(baseCommand, args.subList(1, args.size))
        } else {
            MusicBotCommand(baseCommand)
        }
    }
}

enum class MusicBotBaseCommand(val rawCommand : String) {
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop"),
    CLEAR_QUEUE("clearqueue"),
    SKIP("skip"),
    QUEUE("queue"),
    SHUFFLE("shuffle");

    companion object {
        private val map = values().associateBy(MusicBotBaseCommand::rawCommand)
        fun fromString(rawCommand: String) = map[rawCommand]
    }
}

data class MusicBotCommand(val baseCommand: MusicBotBaseCommand, val urls: List<String> = emptyList())
