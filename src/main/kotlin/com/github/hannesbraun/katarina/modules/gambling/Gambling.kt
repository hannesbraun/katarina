package com.github.hannesbraun.katarina.modules.gambling

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.displayName
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.RuntimeException
import kotlin.random.Random

class Gambling(private val config: KatarinaConfiguration) : KatarinaModule(), MessageReceivedHandler {
    private val parser = GamblingParser(config)

    // Roulette constants
    private val red = listOf(32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3)
    private val black = listOf(15, 4, 2, 17, 6, 13, 11, 8, 10, 24, 33, 20, 31, 22, 29, 28, 35, 26)
    private val green = listOf(0)

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val command = parser.parse(event.message.contentRaw) ?: return false

        when (command.type) {
            GamblingType.RPS -> rps(event, command)
            GamblingType.ROULETTE -> roulette(event)
        }
        return true
    }

    private fun rps(event: MessageReceivedEvent, command: GamblingCommand) {
        val botChoice = RPC.random()
        val userIsWinner = if (botChoice == command.rpc) {
            null
        } else (command.rpc == RPC.ROCK && botChoice == RPC.SCISSORS)
                || (command.rpc == RPC.PAPER && botChoice == RPC.ROCK)
                || (command.rpc == RPC.SCISSORS && botChoice == RPC.PAPER)
        val winnerString = when (userIsWinner) {
            null -> "Nobody"
            true -> event.displayName()
            false -> config.botName
        }

        event.channel.sendMessage(
            EmbedBuilder()
                .setTitle("Rock paper scissors")
                .setColor(0x398b18)
                .addField("${event.displayName()}'s shape", "`" + command.rpc.output + "`", true)
                .addField("${config.botName}'s shape", "`" + botChoice.output + "`", true)
                .addField("Winner", winnerString, true)
                .build()
        ).queue()
    }

    private fun roulette(event: MessageReceivedEvent) {
        val result = Random.nextInt(0, 37)

        val color = when (result) {
            in red -> Pair(0xff0000, "`Red`")
            in black -> Pair(0x000000, "`Black`")
            in green -> Pair(0x0b5602, "`Green`")
            else -> throw RuntimeException("Roulette result was neither red nor black nor green")
        }
        val parity = if (result % 2 == 0) "`Even`" else "`Odd`"
        val section = if (result <= 18) "`Low`" else "`High`"

        event.channel.sendMessage(
            EmbedBuilder()
                .setTitle("Roulette")
                .setColor(color.first)
                .setDescription("**Outcome:** $result")
                .addField("Color", color.second, true)
                .addField("Parity", parity, true)
                .addField("Section", section, true)
                .build()
        ).queue()
    }
}