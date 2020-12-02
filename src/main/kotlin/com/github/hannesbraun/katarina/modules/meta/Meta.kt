package com.github.hannesbraun.katarina.modules.meta

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.KatarinaMeta
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.System.currentTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class Meta(config: KatarinaConfiguration) : KatarinaModule(), MessageReceivedHandler {
    private val parser = MetaParser(config)

    private val startTime = currentTimeMillis()

    private val aboutMessage = """Katarina version ${KatarinaMeta.version}\n
                                  Copyright © 2020 Hannes Braun\n\n
                                  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n\n
                                  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details: https://www.gnu.org/licenses/agpl-3.0-standalone.html"""
        .trimIndent()

    private val helpMessage =
        "An overview about the available commands is available here: https://github.com/hannesbraun/katarina/blob/master/README.md"

    private val sourceMessage = "Have a look at the code I'm made of: https://github.com/hannesbraun/katarina"

    @ExperimentalTime
    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val metaCommand = parser.parse(event.message.contentRaw) ?: return false

        event.channel.sendMessage(
            when (metaCommand) {
                MetaCommand.ABOUT -> aboutMessage
                MetaCommand.HELP -> helpMessage
                MetaCommand.SOURCE -> sourceMessage
                MetaCommand.UPTIME -> "Uptime: ${
                    (currentTimeMillis() - startTime).toDuration(DurationUnit.MILLISECONDS)
                        .toString(DurationUnit.DAYS, 3)
                } days"
            }
        ).queue()
        return true
    }
}