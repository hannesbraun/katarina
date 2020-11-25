package com.github.hannesbraun.katarina.modules

import com.github.hannesbraun.katarina.utilities.KatarinaParsingException

abstract class KatarinaParser {
    private val prefix = "!"

    fun splitArgs(message: String) : List<String> {
        val args = message.removePrefix(prefix).split(" ")
        if (args.isEmpty()) {
            throw KatarinaParsingException("Message does not contain an argument: $message")
        } else {
            return args
        }
    }
}
