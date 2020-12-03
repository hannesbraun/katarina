package com.github.hannesbraun.katarina.modules.math

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.KatarinaParsingException
import java.lang.NumberFormatException

class MathParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): Calculation? {
        val args = splitArgs(message)
        if (args[0].toLowerCase() != "math")
            return null

        if (args.size < 4)
            throw KatarinaParsingException("Not enough arguments. Usage: `!math <number1> <operator> <number2>`")

        val arg1 = try {
            args[1].toDouble()
        } catch (e: NumberFormatException) {
            throw KatarinaParsingException("${args[1]} is not a number")
        }

        val arg2 = try {
            args[3].toDouble()
        } catch (e: NumberFormatException) {
            throw KatarinaParsingException("${args[3]} is not a number")
        }

        val operator = when (args[2]) {
            "+" -> Operator.PLUS
            "-" -> Operator.MINUS
            "*" -> Operator.MULTIPLY
            "/" -> Operator.DIVIDE
            "%" -> Operator.MODULO
            else -> throw KatarinaParsingException("Invalid operator: ${args[3]}")
        }

        return Calculation(arg1, arg2, operator)
    }
}

data class Calculation(val value1: Double, val value2: Double, val operator: Operator)

enum class Operator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO
}
