package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionValidatorTest {

    @Test
    fun check_acceptsGoodSession() {
        assertNull(SessionValidator.check("Judging", "CPD-LG.01", "9:30", "10:15"))
    }

    @Test
    fun check_rejectsBlankTitle() {
        assertEquals(
            "Complete all session fields.",
            SessionValidator.check(" ", "CPD-LG.01", "9:30", "10:15")
        )
    }

    @Test
    fun check_rejectsBadTime() {
        assertEquals(
            "Use 24-hour times like 9:30.",
            SessionValidator.check("Judging", "CPD-LG.01", "half nine", "10:15")
        )
    }

    @Test
    fun check_rejectsEndBeforeStart() {
        assertEquals(
            "End time must be after start time.",
            SessionValidator.check("Judging", "CPD-LG.01", "10:15", "9:30")
        )
    }
}
