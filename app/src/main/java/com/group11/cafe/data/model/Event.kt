package com.group11.cafe.data.model

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val organizerId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)