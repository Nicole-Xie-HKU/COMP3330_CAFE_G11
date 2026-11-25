package com.group11.cafe.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    private val formatter =
        DateTimeFormatter.ofPattern("H:mm")

    fun addMinutes(
        time: String,
        minutes: Long
    ): String {

        return try {

            val parsed =
                LocalTime.parse(
                    time.trim(),
                    formatter
                )

            parsed
                .plusMinutes(minutes)
                .format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )

        } catch (e: Exception) {

            time
        }
    }

    fun isDate(date: String): Boolean {
        return try {
            LocalDate.parse(date.trim())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun toMinutes(time: String): Int? {
        return try {
            val parsed = LocalTime.parse(time.trim(), formatter)
            parsed.hour * 60 + parsed.minute
        } catch (e: Exception) {
            null
        }
    }

    fun isAtOrAfter(
        time: String,
        otherTime: String
    ): Boolean {
        return try {
            LocalTime.parse(time.trim(), formatter) >=
                LocalTime.parse(otherTime.trim(), formatter)
        } catch (e: Exception) {
            false
        }
    }
}