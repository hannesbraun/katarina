package com.github.hannesbraun.katarina.modules.admin

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class AdministrationParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): AdministrationCommand? {
        val args = splitArgs(message)
        return when (args[0].toLowerCase()) {
            "clear" -> AdministrationCommand(
                AdministrationCommandType.CLEAR, intArg = try {
                    args[1].toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            )
            // "createdummy" -> AdministrationCommand(AdministrationCommandType.CREATEDUMMY) // For debugging only
            "mute" -> AdministrationCommand(AdministrationCommandType.MUTE)
            "shutdown" -> AdministrationCommand(AdministrationCommandType.SHUTDOWN)
            "slowmode" -> AdministrationCommand(AdministrationCommandType.SLOWMODE)
            "unmute" -> AdministrationCommand(AdministrationCommandType.UNMUTE)
            else -> null
        }
    }
}

data class AdministrationCommand(val type: AdministrationCommandType, val intArg: Int = 0)

enum class AdministrationCommandType {
    CLEAR,
    CREATEDUMMY,
    MUTE,
    SHUTDOWN,
    SLOWMODE,
    UNMUTE
}
