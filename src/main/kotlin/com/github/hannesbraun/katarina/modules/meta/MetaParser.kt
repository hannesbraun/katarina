package com.github.hannesbraun.katarina.modules.meta

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class MetaParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String) = MetaCommand.fromString(splitArgs(message)[0].toLowerCase())
}

enum class MetaCommand(val commandRaw: String) {
    HELP("help"),
    ABOUT("about"),
    SOURCE("katarina-source"),
    UPTIME("uptime");

    companion object {
        private val map = values().associateBy(MetaCommand::commandRaw)
        fun fromString(commandRaw: String) = map[commandRaw]
    }
}
