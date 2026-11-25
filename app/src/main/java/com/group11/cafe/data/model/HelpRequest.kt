package com.group11.cafe.data.model

// open, claimed or done
data class HelpRequest(
    val id: String = "",
    val eventId: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val place: String = "",
    val note: String = "",
    val status: String = "open",
    val crewName: String = "",
    val createdAt: Long = 0
)
