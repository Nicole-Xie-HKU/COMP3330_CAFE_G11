package com.group11.cafe.data.model

// -30 = half an hour before the session
data class Task(
    val id: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val eventDate: String = "",
    val sessionId: String = "",
    val crewId: String = "",
    val crewName: String = "",
    val title: String = "",
    val offsetMinutes: Int = 0,
    val done: Boolean = false
)
