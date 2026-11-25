package com.group11.cafe.utils

object EventValidator {

    fun check(
        name: String,
        description: String,
        date: String
    ): String? {
        if (name.isBlank()) {
            return "Enter an event name."
        }

        if (!TimeUtils.isDate(date)) {
            return "Use a date like 2026-11-30."
        }

        return null
    }
}
