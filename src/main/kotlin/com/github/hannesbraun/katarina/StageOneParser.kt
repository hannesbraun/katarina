package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.modules.rlc.Rlc
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class StageOneParser : ListenerAdapter() {
    private val modules = listOf<KatarinaModule>(Rlc())

    override fun onMessageReceived(event: MessageReceivedEvent) {
        for (module in modules) {
            if (module is MessageReceivedHandler) {
                if (module.canHandleMessageReceived(event)) module.handleMessageReceived(event)
            }
        }
    }
}