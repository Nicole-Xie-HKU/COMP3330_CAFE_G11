package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EventValidatorTest {

    @Test
    fun check_acceptsGoodEvent() {
        assertNull(EventValidator.check("Judging", "Finals", "2026-11-30"))
    }

    @Test
    fun check_rejectsBlankName() {
        assertEquals(
            "Enter an event name.",
            EventValidator.check("  ", "Finals", "2026-11-30")
        )
    }

    @Test
    fun check_rejectsBadDate() {
        assertEquals(
            "Use a date like 2026-11-30.",
            EventValidator.check("Judging", "Finals", "30 Nov 2026")
        )
    }

    @Test
    fun check_allowsEmptyDescription() {
        assertNull(EventValidator.check("Judging", "", "2026-11-30"))
    }
}
