package com.group11.cafe.data.model

data class Task(
    val id: String = "",
    val title: String = "",
    val dueAt: String = "",
    val organizerId: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val assignedCrewId: String = "",
    val assignedCrewName: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
