package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.modules.rlc.Rlc
import com.github.hannesbraun.katarina.utilities.KatarinaException
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class StageOneParser : ListenerAdapter() {
    private val modules = listOf<KatarinaModule>(Rlc())

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot)
            return

        for (module in modules) {
            if (module !is MessageReceivedHandler)
                continue

            val handled = try {
                module.tryHandleMessageReceived(event)
            } catch (e : KatarinaException) {
                event.channel.sendMessage("${e.javaClass.name}: $e.localizedMessage")
                true
            }

            if (handled)
                return
        }
    }
}