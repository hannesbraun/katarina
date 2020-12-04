package com.github.hannesbraun.katarinatest.utilities

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.github.hannesbraun.katarina.utilities.removeRedundantSpaces
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StringExtensionsTest {
    @Test
    fun testLimit() {
        Assertions.assertEquals("aaa", "aaaa".limit(3))
        Assertions.assertEquals("aaa", "aaa".limit(3))
        Assertions.assertEquals("aaa", "aaa".limit(4))
    }

    @Test
    fun testLimitWithDots() {
        Assertions.assertEquals("aaaaaa", "aaaaaa".limitWithDots(6))
        Assertions.assertEquals("aaa...", "aaaaaaa".limitWithDots(6))
        Assertions.assertEquals("aaaaa", "aaaaa".limitWithDots(6))
    }

    @Test
    fun testRemoveRedundantSpaces() {
        Assertions.assertEquals(" aaa aaa ", "  aaa  aaa   ".removeRedundantSpaces())
        Assertions.assertEquals("a", "a".removeRedundantSpaces())
        Assertions.assertEquals("a a", "a a".removeRedundantSpaces())
    }
}
