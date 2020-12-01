package com.github.hannesbraun.katarina.modules

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.utilities.KatarinaParsingException
import com.github.hannesbraun.katarina.utilities.removeRedundantSpaces

abstract class KatarinaParser(protected val config: KatarinaConfiguration) {
    fun splitArgs(message: String): List<String> {
        val args = message.trim().removeRedundantSpaces().removePrefix(config.prefix).split(" ")
        if (args.isEmpty()) {
            throw KatarinaParsingException("Message does not contain an argument: $message")
        } else {
            return args
        }
    }
}
