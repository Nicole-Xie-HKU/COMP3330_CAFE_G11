package com.group11.cafe.utils

import com.group11.cafe.data.model.Session

object SessionValidator {

    fun check(
        title: String,
        location: String,
        startTime: String,
        endTime: String
    ): String? {
        if (title.isBlank() || location.isBlank()) {
            return "Complete all session fields."
        }

        val start = TimeUtils.toMinutes(startTime)
        val end = TimeUtils.toMinutes(endTime)

        if (start == null || end == null) {
            return "Use 24-hour times like 9:30."
        }

        if (end <= start) {
            return "End time must be after start time."
        }

        return null
    }

    fun overlap(
        sessions: List<Session>,
        startTime: String,
        endTime: String,
        ignoreSessionId: String?
    ): String? {
        val start = TimeUtils.toMinutes(startTime) ?: return null
        val end = TimeUtils.toMinutes(endTime) ?: return null

        val hit = sessions
            .filter { it.id != ignoreSessionId }
            .filter { session ->
                val otherStart = TimeUtils.toMinutes(session.startTime) ?: return@filter false
                val otherEnd = TimeUtils.toMinutes(session.endTime) ?: return@filter false
                start < otherEnd && otherStart < end
            }
            .minByOrNull { TimeUtils.toMinutes(it.startTime) ?: Int.MAX_VALUE }

        return hit?.let { "Overlaps with ${it.title}." }
    }
}
