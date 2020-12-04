package com.github.hannesbraun.katarina.modules.ccs

data class ParsedClassicCommand(
    val name: String,
    val meta: Boolean,
    val affectedCommand: String = "",
    val action: SetccAction? = null,
    val setccValue: String = "",
    val restrictionType: RestrictionType? = null,
    val affectedId: String = ""
)

object ClassicMetaCommand {
    const val set = "setcc"
    const val remove = "rmcc"
    const val data = "ccdata"
    const val help = "ccs-help"
}

enum class SetccAction(val action: String) {
    MESSAGE("m"),
    ACTIVE("a"),
    NSFW("nsfw"),
    DESCRIPTION("d"),
    RESTRICTION("r"),
    DELETE_RESTRICTION("rmr");

    companion object {
        private val map = values().associateBy(SetccAction::action)
        fun fromString(action: String) = map[action]
    }
}

enum class RestrictionType(val restriction: String) {
    USER_WHITELIST("uw"),
    USER_BLACKLIST("ub"),
    CHANNEL_WHITELIST("cw"),
    CHANNEL_BLACKLIST("cb");

    companion object {
        private val map = values().associateBy(RestrictionType::restriction)
        fun fromString(restriction: String) = map[restriction]
    }
}
