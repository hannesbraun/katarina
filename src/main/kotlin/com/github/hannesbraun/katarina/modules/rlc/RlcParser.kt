package com.github.hannesbraun.katarina.modules.rlc

import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.limit

class RlcParser : KatarinaParser() {
    private val command = "rlc"

    fun parse(message: String) : RlcCommand? {
        val args = splitArgs(message)
        if (args[0].toLowerCase() != command)
            return null

        val numberOfChampions = try {
            if (args.size > 1) args[1].toInt().limit(1, 20) else 1
        } catch (e : NumberFormatException) {
            1
        }

        return RlcCommand(numberOfChampions)
    }
}

data class RlcCommand(val numberOfChampions: Int = 20)
