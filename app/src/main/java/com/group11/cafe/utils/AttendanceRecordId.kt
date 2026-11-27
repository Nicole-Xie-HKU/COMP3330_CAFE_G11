package com.group11.cafe.utils

object AttendanceRecordId {

    fun create(
        eventId: String,
        sessionId: String,
        participantId: String
    ): String {
        return "${eventId}_${sessionId}_${participantId}"
    }
}
