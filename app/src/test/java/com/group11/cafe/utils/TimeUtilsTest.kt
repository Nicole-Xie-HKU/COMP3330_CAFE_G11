package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun addMinutes_shiftsTimeForward() {
        assertEquals("10:30", TimeUtils.addMinutes("10:00", 30))
        assertEquals("11:15", TimeUtils.addMinutes("10:45", 30))
    }

    @Test
    fun isAtOrAfter_usesTimeInsteadOfTextOrder() {
        assertTrue(TimeUtils.isAtOrAfter("10:00", "9:30"))
        assertFalse(TimeUtils.isAtOrAfter("9:30", "10:00"))
    }

    @Test
    fun toMinutes_sortsNineBeforeTen() {
        val times = listOf("10:00", "9:30", "14:15")

        assertEquals(
            listOf("9:30", "10:00", "14:15"),
            times.sortedBy { TimeUtils.toMinutes(it) }
        )
    }

    @Test
    fun toMillis_needsGoodDateAndTime() {
        val start = TimeUtils.toMillis("2026-11-30", "9:30")!!
        val end = TimeUtils.toMillis("2026-11-30", "10:00")!!

        assertEquals(30 * 60 * 1000L, end - start)
        assertEquals(null, TimeUtils.toMillis("", "9:30"))
    }
}
