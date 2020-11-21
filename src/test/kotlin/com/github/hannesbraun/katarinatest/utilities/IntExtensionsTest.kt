package com.github.hannesbraun.katarinatest.utilities

import com.github.hannesbraun.katarina.utilities.limit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IntExtensionsTest {
    @Test
    fun testLimit() {
        Assertions.assertEquals(42, 42.limit(0, 50))
        Assertions.assertEquals(0, (-61).limit(0, 50))
        Assertions.assertEquals(50, 142.limit(0, 50))
        Assertions.assertEquals(100, 100.limit(5,-5))
    }
}