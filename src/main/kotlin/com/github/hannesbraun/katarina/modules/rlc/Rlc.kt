package com.github.hannesbraun.katarina.modules.rlc

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Rlc : KatarinaModule(), MessageReceivedHandler {
    private val parser = RlcParser()

    private val champions = Champions()

    override fun tryHandleMessageReceived(event: MessageReceivedEvent) : Boolean {
        val command = parser.parse(event.message.contentRaw) ?: return false

        val result = champions.getRandomChampions(command.numberOfChampions)
        val resultMessage = result.joinToString("\n")
        event.channel.sendMessage(resultMessage).queue()
        return true
    }
}