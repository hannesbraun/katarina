package com.github.hannesbraun.katarina.modules

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageReceivedHandler {
    fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean
}