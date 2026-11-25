package com.group11.cafe.utils

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
}
