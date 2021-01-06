package com.github.hannesbraun.katarina.utilities

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.util.concurrent.TimeUnit

fun MessageReceivedEvent.displayName(): String =
    if (this.isFromGuild)
        member?.effectiveName ?: "null"
    else
        author.name

fun Message.deleteAfter(minutes: Long) {
    this.delete().queueAfter(minutes, TimeUnit.MINUTES)
}

fun MessageAction.deleteAfter(minutes: Long) =
    this.delay(minutes, TimeUnit.MINUTES).flatMap { it.delete() }


fun MessageAction.deleteAfter(duration: Long, unit: TimeUnit) =
    this.delay(duration, unit).flatMap { it.delete() }
