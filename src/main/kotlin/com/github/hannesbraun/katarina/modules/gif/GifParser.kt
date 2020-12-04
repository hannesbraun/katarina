package com.github.hannesbraun.katarina.modules.gif

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class GifParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): Gif? = Gif.fromString(splitArgs(message)[0].toLowerCase())
}

enum class Gif(val asString: String, val verb: String = "") {
    BITE("bite", "bites"),
    CAKE("cake", "feeds"),
    CONFUSED("confused"),
    CRY("cry"),
    CUDDLE("cuddle", "cuddles"),
    GLARE("glare", "glares at"),
    HIGH_FIVE("highfive", "high-fives"),
    HUG("hug", "hugs"),
    KISS("kiss", "kisses"),
    LEWD("lewd"),
    LICK("lick", "licks"),
    PAT("pat", "pats"),
    POKE("poke", "pokes"),
    POUT("pout"),
    PUNCH("punch", "punches"),
    SLAP("slap", "slaps"),
    SMUG("smug"),
    STARE("stare");

    companion object {
        private val map = values().associateBy(Gif::asString)
        fun fromString(commandString: String) = map[commandString]
    }
}
