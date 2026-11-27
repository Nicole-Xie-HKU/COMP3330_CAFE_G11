package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceRecordIdTest {

    @Test
    fun create_generatesExpectedRecordId() {
        assertEquals(
            "event1_session1_user1",
            AttendanceRecordId.create("event1", "session1", "user1")
        )
    }

    @Test
    fun create_generatesSameIdForSameParticipantAndSession() {
        val first = AttendanceRecordId.create("event1", "session1", "user1")
        val second = AttendanceRecordId.create("event1", "session1", "user1")

        assertEquals(first, second)
    }

    @Test
    fun create_generatesDifferentIdsForDifferentParticipants() {
        val first = AttendanceRecordId.create("event1", "session1", "user1")
        val second = AttendanceRecordId.create("event1", "session1", "user2")

        assertNotEquals(first, second)
    }

    @Test
    fun create_generatesDifferentIdsForDifferentSessions() {
        val first = AttendanceRecordId.create("event1", "session1", "user1")
        val second = AttendanceRecordId.create("event1", "session2", "user1")

        assertNotEquals(first, second)
    }

    @Test
    fun create_generatesDifferentIdsForDifferentEvents() {
        val first = AttendanceRecordId.create("event1", "session1", "user1")
        val second = AttendanceRecordId.create("event2", "session1", "user1")

        assertNotEquals(first, second)
    }

    private fun assertNotEquals(expected: String, actual: String) {
        org.junit.Assert.assertNotEquals(expected, actual)
    }
}
