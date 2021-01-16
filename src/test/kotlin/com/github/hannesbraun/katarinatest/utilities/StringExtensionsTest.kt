package com.github.hannesbraun.katarinatest.utilities

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.github.hannesbraun.katarina.utilities.removeRedundantSpaces
import org.junit.Test
import kotlin.test.assertEquals

class StringExtensionsTest {
    @Test
    fun testLimit() {
        assertEquals("aaa", "aaaa".limit(3))
        assertEquals("aaa", "aaa".limit(3))
        assertEquals("aaa", "aaa".limit(4))
    }

    @Test
    fun testLimitWithDots() {
        assertEquals("aaaaaa", "aaaaaa".limitWithDots(6))
        assertEquals("aaa...", "aaaaaaa".limitWithDots(6))
        assertEquals("aaaaa", "aaaaa".limitWithDots(6))
    }

    @Test
    fun testRemoveRedundantSpaces() {
        assertEquals(" aaa aaa ", "  aaa  aaa   ".removeRedundantSpaces())
        assertEquals("a", "a".removeRedundantSpaces())
        assertEquals("a a", "a a".removeRedundantSpaces())
    }
}
