package com.github.hannesbraun.katarina.modules.rlc

import com.github.hannesbraun.katarina.modules.KatarinaParser
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RlcParser : KatarinaParser() {
    val command = "rlc"

    fun canHandle(event: MessageReceivedEvent) : Boolean {
        val msg = event.message.contentRaw
        if (!super.canHandle(msg)) return false

        val args = splitArgs(msg)
        return args.isNotEmpty() && args[0].equals(command, ignoreCase = true)
    }

    fun getNumberOfChampions(event: MessageReceivedEvent) : Int {
        val msg = event.message.contentRaw
        val args = splitArgs(msg)
        return if (args.size > 1) args[1].toInt() else 1
    }
}
