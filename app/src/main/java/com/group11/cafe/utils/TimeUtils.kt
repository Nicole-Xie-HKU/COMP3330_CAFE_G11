package com.group11.cafe.utils

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
}