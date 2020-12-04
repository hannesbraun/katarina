package com.github.hannesbraun.katarina.modules.gif

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.database.Gif
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.displayName
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class GifModule(private val config: KatarinaConfiguration) : KatarinaModule(), MessageReceivedHandler {
    private val parser = GifParser(config)

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val command = parser.parse(event.message.contentRaw) ?: return false

        val nsfwChannel =
            if (event.isFromGuild)
                event.textChannel.isNSFW
            else
                false

        val gifs = transaction {
            Gif.select {
                if (nsfwChannel) {
                    (Gif.command eq command.asString) and (Gif.active eq 1)
                } else {
                    (Gif.command eq command.asString) and (Gif.active eq 1) and (Gif.nsfw eq 0)
                }
            }.toList()
        }

        if (gifs.isEmpty()) {
            event.channel.sendMessage("No gif was found for this command.").queue()
        } else {
            sendGif(gifs, command, event)
        }

        return true
    }

    private fun sendGif(
        gifs: List<ResultRow>,
        command: com.github.hannesbraun.katarina.modules.gif.Gif,
        event: MessageReceivedEvent
    ) {
        val gifUrl = gifs.random()[Gif.url]
        val embedBuilder = EmbedBuilder()
            .setColor(0x4a6cac)
            .setImage(gifUrl)
        if (command.verb.isNotBlank()) {
            // Active command -> we need a title
            val authorName = event.displayName()
            val title = when (event.message.mentionedMembers.size) {
                0 -> "Katarina ${command.verb} $authorName" // Katarina -> User
                1 -> "$authorName ${command.verb} ${event.message.mentionedMembers.first().effectiveName}" // User -> other user
                else -> {
                    // User -> multiple users
                    "$authorName ${command.verb} ${
                        event.message.mentionedMembers
                            .subList(0, event.message.mentionedMembers.size - 1)
                            .joinToString(", ") { it.effectiveName }
                    } and ${event.message.mentionedMembers.last().effectiveName}"
                }
            }
            embedBuilder.setTitle(title)
        }

        event.channel.sendMessage(embedBuilder.build()).queue()
    }
}
