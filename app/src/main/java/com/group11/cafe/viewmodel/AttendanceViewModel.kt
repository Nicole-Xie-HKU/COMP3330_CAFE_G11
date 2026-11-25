package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.AttendanceRecord
import com.group11.cafe.data.model.User
import com.group11.cafe.data.repository.AttendanceRepository
import com.group11.cafe.data.repository.UserRepository

class AttendanceViewModel : ViewModel() {

    private val repository = AttendanceRepository()
    private val userRepository = UserRepository()

    fun getParticipant(
        participantId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userRepository.getUserById(
            participantId,
            onSuccess,
            onFailure
        )
    }

    fun listenForSession(
        eventId: String,
        sessionId: String,
        onChange: (List<AttendanceRecord>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listenForSession(
            eventId,
            sessionId,
            onChange,
            onFailure
        )
    }

    fun getAttendanceForSession(
        eventId: String,
        sessionId: String,
        onSuccess: (List<AttendanceRecord>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getAttendanceForSession(
            eventId,
            sessionId,
            onSuccess,
            onFailure
        )
    }

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
