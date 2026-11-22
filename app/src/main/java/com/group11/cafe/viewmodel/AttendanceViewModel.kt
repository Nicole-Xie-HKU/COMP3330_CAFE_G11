package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.repository.AttendanceRepository

class AttendanceViewModel : ViewModel() {

    private val repository = AttendanceRepository()

    fun checkIn(
        eventId: String,
        sessionId: String,
        participantId: String,
        participantName: String,
        checkedInBy: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.checkIn(
            eventId,
            sessionId,
            participantId,
            participantName,
            checkedInBy,
            onSuccess,
            onFailure
        )
    }
}
