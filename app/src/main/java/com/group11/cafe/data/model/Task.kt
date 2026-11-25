package com.group11.cafe.data.model

// Negative offsets schedule a task before the session starts.
data class Task(
    val id: String = "",
    val title: String = "",
    val dueAt: String = "",
    val organizerId: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val eventDate: String = "",
    val sessionId: String = "",
    val assignedCrewId: String = "",
    val assignedCrewName: String = "",
    val crewId: String = "",
    val crewName: String = "",
    val offsetMinutes: Int = 0,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val done: Boolean = false
)
