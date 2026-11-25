package com.group11.cafe.data.model

data class Session(
    val id: String = "",
    val eventId: String = "",
    val title: String = "",
    val location: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val fixedTime: Boolean = false,
    val movedMinutes: Int = 0
)