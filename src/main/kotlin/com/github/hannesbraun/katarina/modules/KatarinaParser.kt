package com.github.hannesbraun.katarina.modules

import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

open abstract class KatarinaParser {
    private val prefix = "!"

    fun canHandle(message: String) : Boolean = message.startsWith(prefix)
    fun splitArgs(message: String) : List<String> = message.removePrefix(prefix).split(" ")
}
