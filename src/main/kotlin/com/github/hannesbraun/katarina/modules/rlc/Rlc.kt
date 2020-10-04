package com.github.hannesbraun.katarina.modules.rlc

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Rlc : KatarinaModule(), MessageReceivedHandler {
    private val parser = RlcParser()

    private val champions = Champions()

    override fun canHandleMessageReceived(event: MessageReceivedEvent): Boolean = parser.canHandle(event)

    override fun handleMessageReceived(event: MessageReceivedEvent) {
        var i : Int = try {
            parser.getNumberOfChampions(event)
        } catch (e : NumberFormatException) {
            0
        }
        if (i > 20) i = 20

        val result = champions.getRandomChampions(i)
        val resultMessage = result.joinToString("\n")
        event.channel.sendMessage(resultMessage)
    }
}