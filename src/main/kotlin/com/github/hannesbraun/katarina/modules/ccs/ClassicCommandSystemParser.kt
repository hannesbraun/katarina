package com.github.hannesbraun.katarina.modules.ccs

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser
import com.github.hannesbraun.katarina.utilities.KatarinaCCSException

class ClassicCommandSystemParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): ParsedClassicCommand {
        val args = splitArgs(message)
        return if (args[0] == ClassicMetaCommand.set) {
            // setcc
            if (args.size < 4) throw KatarinaCCSException("Not enough arguments. Usage: `${config.prefix}setcc <command> <action> <value>`")

            val command = args[1]
            val action =
                SetccAction.fromString(args[2])
                    ?: throw KatarinaCCSException("${args[2]}: Invalid action for `${config.prefix}setcc`.")

            if (action == SetccAction.RESTRICTION || action == SetccAction.DELETE_RESTRICTION && args.size < 5) throw KatarinaCCSException(
                "Not enough arguments. Usage: `${config.prefix}setcc <command> <action> <value>`"
            )

            val value = when (action) {
                SetccAction.MESSAGE -> args.subList(3, args.size).joinToString(" ")
                SetccAction.ACTIVE -> args[3]
                SetccAction.NSFW -> args[3]
                SetccAction.DESCRIPTION -> args.subList(3, args.size).joinToString(" ")
                SetccAction.RESTRICTION -> ""
                SetccAction.DELETE_RESTRICTION -> ""
            }

            val restrictionType =
                if (action == SetccAction.DELETE_RESTRICTION || action == SetccAction.RESTRICTION) {
                    RestrictionType.fromString(args[3])
                } else {
                    null
                }
            val userId =
                if (action == SetccAction.DELETE_RESTRICTION || action == SetccAction.RESTRICTION) {
                    args[4]
                } else {
                    ""
                }

            ParsedClassicCommand(args[0], true, command, action, value, restrictionType, userId)
        } else if (args[0] == ClassicMetaCommand.remove) {
            if (args.size < 2) throw KatarinaCCSException("Not enough arguments. Usage: `${config.prefix}rmcc <command>`")
            ParsedClassicCommand(args[0], true, args[1])
        } else if (args[0] == ClassicMetaCommand.data) {
            if (args.size < 2) throw KatarinaCCSException("Not enough arguments. Usage: `${config.prefix}ccdata <command>`")
            ParsedClassicCommand(args[0], true, args[1])
        } else if (args[0] == ClassicMetaCommand.help) {
            ParsedClassicCommand(args[0], true)
        } else {
            ParsedClassicCommand(args[0], false)
        }
    }
}
