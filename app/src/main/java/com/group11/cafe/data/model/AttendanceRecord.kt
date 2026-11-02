package com.group11.cafe.data.model

import com.google.firebase.Timestamp

data class AttendanceRecord(
    val id: String = "",
    val eventId: String = "",
    val sessionId: String = "",
    val participantId: String = "",
    val participantName: String = "",
    val checkedInBy: String = "",
    val checkedInAt: Timestamp? = null
)