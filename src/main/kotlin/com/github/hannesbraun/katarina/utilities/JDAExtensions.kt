package com.github.hannesbraun.katarina.utilities

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun MessageReceivedEvent.displayName(): String =
    if (this.isFromGuild)
        member?.effectiveName ?: "null"
    else
        author.name