package com.group11.cafe.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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

    fun toMillis(date: String, time: String): Long? {
        return try {
            LocalDate.parse(date.trim())
                .atTime(LocalTime.parse(time.trim(), formatter))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }

    fun timeLeft(date: String, time: String, now: Long): String {
        val target = toMillis(date, time) ?: return ""
        val minutes = (target - now) / 60000

        return when {
            target < now -> "overdue"
            minutes < 60 -> "in $minutes min"
            minutes < 24 * 60 -> "in ${minutes / 60} h"
            else -> "in ${minutes / (24 * 60)} days"
        }
    }

    fun isNow(date: String, startTime: String, endTime: String, now: Long): Boolean {
        val start = toMillis(date, startTime) ?: return false
        val end = toMillis(date, endTime) ?: return false

        return now >= start && now < end
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