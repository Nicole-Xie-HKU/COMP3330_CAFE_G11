package com.group11.cafe.utils

import com.group11.cafe.data.model.Session
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

    @Test
    fun overlap_namesTheSessionItHits() {
        val sessions = listOf(
            Session(id = "later", title = "Awards", startTime = "11:00", endTime = "12:00"),
            Session(id = "judging", title = "Judging", startTime = "09:30", endTime = "10:15")
        )

        assertEquals(
            "Overlaps with Judging.",
            SessionValidator.overlap(sessions, "10:00", "11:30", null)
        )
    }

    @Test
    fun overlap_allowsTouchingEndpoints() {
        val sessions = listOf(
            Session(id = "1", title = "Judging", startTime = "10:15", endTime = "11:00")
        )

        assertNull(SessionValidator.overlap(sessions, "11:00", "12:00", null))
    }

    @Test
    fun overlap_ignoresTheSessionBeingEdited() {
        val sessions = listOf(
            Session(id = "judging", title = "Judging", startTime = "09:30", endTime = "10:15")
        )

        assertNull(
            SessionValidator.overlap(sessions, "09:30", "10:15", "judging")
        )
    }

    @Test
    fun overlap_reportsAFixedSession() {
        val sessions = listOf(
            Session(
                id = "lunch",
                title = "Lunch",
                startTime = "12:00",
                endTime = "13:00",
                fixedTime = true
            )
        )

        assertEquals(
            "Overlaps with Lunch.",
            SessionValidator.overlap(sessions, "12:30", "12:45", null)
        )
    }

    @Test
    fun overlap_ignoresUnparseableTimes() {
        val sessions = listOf(
            Session(id = "judging", title = "Judging", startTime = "09:30", endTime = "10:15")
        )

        assertNull(SessionValidator.overlap(sessions, "half nine", "10:30", null))
    }
}
