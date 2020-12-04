package com.github.hannesbraun.katarina.modules.joke

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.database.Joke
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Joke(private val database: Database, config: KatarinaConfiguration): KatarinaModule(), MessageReceivedHandler {
    private val parser = JokeParser(config)

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        if (!parser.parse(event.message.contentRaw)) return false

        val jokes = transaction(database) {
            Joke.select { Joke.active eq 1 }.toList()
        }

        if (jokes.isEmpty()) {
            event.channel.sendMessage("I don't know a joke... \ud83d\ude22").queue()
        } else {
            val joke = jokes.random()
            val heading = if (joke[Joke.heading]?.isBlank() != false) {
                ""
            } else {
                "**" + (joke[Joke.heading] ?: "") + "**\n\n"
            }
            event.channel.sendMessage(heading + joke[Joke.text]).queue()
        }

        return true
    }
}