package com.group11.cafe.utils

import com.group11.cafe.data.model.Session
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftUtilsTest {

    private val opening = Session(id = "1", title = "Opening", startTime = "9:00", endTime = "9:30")
    private val judging = Session(id = "2", title = "Judging", startTime = "9:30", endTime = "10:30")
    private val lunch = Session(id = "3", title = "Lunch", startTime = "12:00", endTime = "13:00", fixedTime = true)
    private val awards = Session(id = "4", title = "Awards", startTime = "13:00", endTime = "14:00")
    private val all = listOf(opening, judging, lunch, awards)

    private fun shift(start: Session, minutes: Int): List<Session> {
        return ShiftUtils.move(ShiftUtils.sessionsToMove(all, start), minutes)
    }

    @Test
    fun move_changesLaterSessionsOnly() {
        val moved = shift(judging, 15)

        assertEquals(listOf("2", "4"), moved.map { it.id })
        assertEquals("09:45", moved[0].startTime)
        assertEquals("10:45", moved[0].endTime)
        assertEquals(15, moved[0].movedMinutes)
    }

    @Test
    fun move_canRunEarly() {
        val moved = shift(awards, -15)

        assertEquals("12:45", moved[0].startTime)
        assertEquals(-15, moved[0].movedMinutes)
    }

    @Test
    fun clashes_warnsAboutFixedSession() {
        val before = listOf(awards)

        assertEquals(
            listOf("Lunch"),
            ShiftUtils.clashes(all, ShiftUtils.move(before, -15))
        )
        assertTrue(
            ShiftUtils.clashes(all, ShiftUtils.move(before, 15)).isEmpty()
        )
    }

    @Test
    fun passesMidnight_isDetected() {
        val late = Session(id = "5", startTime = "23:00", endTime = "23:50")

        assertFalse(ShiftUtils.passesMidnight(all, 15))
        assertTrue(ShiftUtils.passesMidnight(listOf(late), 15))
        assertTrue(ShiftUtils.passesMidnight(listOf(opening), -600))
    }
}
