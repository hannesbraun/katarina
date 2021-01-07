package com.github.hannesbraun.katarina.modules.admin

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.KatarinaParsingException
import com.github.hannesbraun.katarina.utilities.limit

class AdministrationParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): AdministrationCommand? {
        val args = splitArgs(message)
        return when (args[0].toLowerCase()) {
            "clear" -> AdministrationCommand(
                AdministrationCommandType.CLEAR, intArg = try {
                    args[1].toInt()
                } catch (e: NumberFormatException) {
                    0
                } catch (e: IndexOutOfBoundsException) {
                    0
                }
            )
            // "createdummy" -> AdministrationCommand(AdministrationCommandType.CREATEDUMMY) // For debugging only
            "mm" -> {
                if (args.size < 3) throw KatarinaParsingException("Not enough arguments")
                AdministrationCommand(AdministrationCommandType.MASSMOVE, strArg1 = args[1], strArg2 = args[2])
            }
            "mute" -> AdministrationCommand(AdministrationCommandType.MUTE)
            "permissions" -> {
                if (args.size < 3) throw KatarinaParsingException("Not enough arguments")
                AdministrationCommand(AdministrationCommandType.SHOWPERMISSIONS, strArg1 = args[2])
            }
            "shutdown" -> AdministrationCommand(AdministrationCommandType.SHUTDOWN)
            "slowmode" -> AdministrationCommand(AdministrationCommandType.SLOWMODE)
            "unmute" -> AdministrationCommand(AdministrationCommandType.UNMUTE)
            else -> null
        }
    }
}

data class AdministrationCommand(
    val type: AdministrationCommandType,
    val intArg: Int = 0,
    val strArg1: String = "",
    val strArg2: String = ""
)

enum class AdministrationCommandType {
    CLEAR,
    CREATEDUMMY,
    MASSMOVE,
    MUTE,
    SHOWPERMISSIONS,
    SHUTDOWN,
    SLOWMODE,
    UNMUTE
}
