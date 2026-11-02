package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.repository.SessionRepository

class SessionViewModel : ViewModel() {

    private val repository =
        SessionRepository()

    fun createSession(
        session: Session,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.createSession(
            session,
            onSuccess,
            onFailure
        )
    }

    fun getSessionsForEvent(
        eventId: String,
        onSuccess: (List<Session>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.getSessionsForEvent(
            eventId,
            onSuccess,
            onFailure
        )
    }

    fun updateSession(
        session: Session,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.updateSession(
            session,
            onSuccess,
            onFailure
        )
    }

    fun deleteSession(
        sessionId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.deleteSession(
            sessionId,
            onSuccess,
            onFailure
        )
    }

    fun getSessionById(
        sessionId: String,
        onSuccess: (Session) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.getSessionById(
            sessionId,
            onSuccess,
            onFailure
        )
    }

    fun getAllSessionsForEvent(
        eventId: String,
        onSuccess: (List<Session>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.getAllSessionsForEvent(
            eventId,
            onSuccess,
            onFailure
        )
    }

    fun updateSessions(
        sessions: List<Session>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.updateSessions(
            sessions,
            onSuccess,
            onFailure
        )
    }

}