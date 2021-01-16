package com.github.hannesbraun.katarinatest.modules.rlc

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.database.ConfigurationConstants
import com.github.hannesbraun.katarina.modules.rlc.Champions
import com.github.hannesbraun.katarina.modules.rlc.RlcParser
import org.junit.Test
import kotlin.test.assertEquals

class RlcTest {
    @Test
    fun testGenerateRandomChampions() {
        // Test amount of generated champions
        assertEquals(0, Champions.getRandomChampions(0).size)
        assertEquals(0, Champions.getRandomChampions(-1).size)
        assertEquals(1, Champions.getRandomChampions(1).size)
        assertEquals(42, Champions.getRandomChampions(42).size)

        // Check for duplicates
        for (i in 1..20) {
            val champions = Champions.getRandomChampions(20)
            assertEquals(20, champions.distinct().size)
        }
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

        assertEquals(null, parser.parse("!rng"))
        assertEquals(1, parser.parse("!rlc")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc 1")?.numberOfChampions)
        assertEquals(19, parser.parse("!rlc 19")?.numberOfChampions)
        assertEquals(20, parser.parse("!rlc 20")?.numberOfChampions)
        assertEquals(20, parser.parse("!rlc 21")?.numberOfChampions)
        assertEquals(20, parser.parse("!rlc 345")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc 0")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc -5")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc lol")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc 0x2")?.numberOfChampions)
        assertEquals(1, parser.parse("!rlc 0xDEADBEEF")?.numberOfChampions)
    }
}