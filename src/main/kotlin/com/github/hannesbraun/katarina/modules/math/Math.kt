package com.github.hannesbraun.katarina.modules.math

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.KatarinaMathException
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Math(config: KatarinaConfiguration) : KatarinaModule(), MessageReceivedHandler {
    private val parser = MathParser(config)

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val calculation = parser.parse(event.message.contentRaw) ?: return false

        val result = when (calculation.operator) {
            Operator.PLUS -> calculation.value1 + calculation.value2
            Operator.MINUS -> calculation.value1 - calculation.value2
            Operator.MULTIPLY -> calculation.value1 * calculation.value2
            Operator.DIVIDE -> {
                if (calculation.value2 == 0.0)
                    throw KatarinaMathException("Division by zero is not possible")
                calculation.value1 / calculation.value2
            }
            Operator.MODULO -> {
                if (calculation.value2 == 0.0)
                    throw KatarinaMathException("Division by zero is not possible")
                calculation.value1 % calculation.value2
            }
        }

        event.channel.sendMessage("**Result:** $result").queue()
        return true
    }
}