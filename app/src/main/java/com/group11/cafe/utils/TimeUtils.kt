package com.group11.cafe.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    private val formatter =
        DateTimeFormatter.ofPattern("HH:mm")

    fun addMinutes(
        time: String,
        minutes: Long
    ): String {

        val parsed =
            LocalTime.parse(
                time,
                formatter
            )

        return parsed
            .plusMinutes(minutes)
            .format(formatter)
    }
}