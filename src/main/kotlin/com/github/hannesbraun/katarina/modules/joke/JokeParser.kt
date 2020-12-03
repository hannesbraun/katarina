package com.github.hannesbraun.katarina.modules.joke

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class JokeParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): Boolean = splitArgs(message)[0].toLowerCase() == "joke"
}