package com.github.hannesbraun.katarina.modules.gambling

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.KatarinaParsingException

class GamblingParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): GamblingCommand? {
        val args = splitArgs(message)
        return when (args[0].toLowerCase()) {
            "rps" -> {
                if (args.size < 2) throw KatarinaParsingException("Not enough arguments. Usage: `${config.prefix}rps {rock | paper | scissors}`")
                val rpc = when (args[1]) {
                    "rock" -> RPC.ROCK
                    "paper" -> RPC.PAPER
                    "scissors" -> RPC.SCISSORS
                    else -> throw KatarinaParsingException("Unrecognized object: ${args[1]}\nUsage: `${config.prefix}rps {rock | paper | scissors}`")
                }
                GamblingCommand(GamblingType.RPS, rpc)
            }
            "roulette" -> GamblingCommand(GamblingType.ROULETTE)
            else -> null
        }
    }
}

data class GamblingCommand(val type: GamblingType, val rpc: RPC = RPC.ROCK)

enum class GamblingType {
    RPS,
    ROULETTE
}

enum class RPC(val output: String) {
    ROCK("Rock"),
    PAPER("Paper"),
    SCISSORS("Scissors");

    companion object {
        private val values = values()
        fun random() = values.random()
    }
}