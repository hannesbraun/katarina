package com.github.hannesbraun.katarinatest.utilities

import com.github.hannesbraun.katarina.utilities.limit
import com.github.hannesbraun.katarina.utilities.toBoolean
import org.junit.Test
import kotlin.test.assertEquals

class IntExtensionsTest {
    @Test
    fun testLimit() {
        assertEquals(42, 42.limit(0, 50))
        assertEquals(0, (-61).limit(0, 50))
        assertEquals(50, 142.limit(0, 50))
        assertEquals(100, 100.limit(5, -5))
    }

    @Test
    fun testToBoolean() {
        assertEquals(true, 1.toBoolean())
        assertEquals(false, 0.toBoolean())
        assertEquals(true, (-1).toBoolean())
    }
}