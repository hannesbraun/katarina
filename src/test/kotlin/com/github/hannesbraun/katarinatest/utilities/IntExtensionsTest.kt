package com.github.hannesbraun.katarinatest.utilities

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.toBoolean
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IntExtensionsTest {
    @Test
    fun testLimit() {
        Assertions.assertEquals(42, 42.limit(0, 50))
        Assertions.assertEquals(0, (-61).limit(0, 50))
        Assertions.assertEquals(50, 142.limit(0, 50))
        Assertions.assertEquals(100, 100.limit(5, -5))
    }

    @Test
    fun testToBoolean() {
        Assertions.assertEquals(true, 1.toBoolean())
        Assertions.assertEquals(false, 0.toBoolean())
        Assertions.assertEquals(true, (-1).toBoolean())
    }
}