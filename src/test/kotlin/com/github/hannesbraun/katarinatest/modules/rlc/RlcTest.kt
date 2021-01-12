package com.github.hannesbraun.katarinatest.modules.rlc

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.database.ConfigurationConstants
import com.github.hannesbraun.katarina.modules.rlc.Champions
import com.github.hannesbraun.katarina.modules.rlc.RlcParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RlcTest {
    @Test
    fun testGenerateRandomChampions() {
        Assertions.assertEquals(0, Champions.getRandomChampions(0).size)
        Assertions.assertEquals(0, Champions.getRandomChampions(-1).size)
        Assertions.assertEquals(1, Champions.getRandomChampions(1).size)
        Assertions.assertEquals(42, Champions.getRandomChampions(42).size)
    }

    @Test
    fun testParser() {
        val parser = RlcParser(
            KatarinaConfiguration(
                ConfigurationConstants.defaultPrefix,
                ConfigurationConstants.defaultOwnerId,
                ConfigurationConstants.defaultBotName,
                ConfigurationConstants.defaultDbVersion
            )
        )

        Assertions.assertEquals(null, parser.parse("!rng"))
        Assertions.assertEquals(1, parser.parse("!rlc")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc 1")?.numberOfChampions)
        Assertions.assertEquals(19, parser.parse("!rlc 19")?.numberOfChampions)
        Assertions.assertEquals(20, parser.parse("!rlc 20")?.numberOfChampions)
        Assertions.assertEquals(20, parser.parse("!rlc 21")?.numberOfChampions)
        Assertions.assertEquals(20, parser.parse("!rlc 345")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc 0")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc -5")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc lol")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc 0x2")?.numberOfChampions)
        Assertions.assertEquals(1, parser.parse("!rlc 0xDEADBEEF")?.numberOfChampions)
    }
}