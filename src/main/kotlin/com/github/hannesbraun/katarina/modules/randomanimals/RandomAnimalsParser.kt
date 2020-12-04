package com.github.hannesbraun.katarina.modules.randomanimals

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class RandomAnimalsParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): Animal? =
        when (splitArgs(message)[0].toLowerCase()) {
            "cat" -> Animal.CAT
            "dog" -> Animal.DOG
            else -> null
        }
}

enum class Animal {
    CAT,
    DOG
}
