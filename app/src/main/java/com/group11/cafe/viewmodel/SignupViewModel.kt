package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.Signup
import com.group11.cafe.data.repository.SignupRepository

class SignupViewModel : ViewModel() {

    private val repository = SignupRepository()

    fun signUp(
        signup: Signup,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.signUp(signup, onSuccess, onFailure)
    }

    fun cancel(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.cancel(eventId, userId, onSuccess, onFailure)
    }

    fun listenForUser(
        userId: String,
        onChange: (List<Signup>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listenForUser(userId, onChange, onFailure)
    }

    fun listenForEvent(
        eventId: String,
        onChange: (List<Signup>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listenForEvent(eventId, onChange, onFailure)
    }

    fun deleteForEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.deleteForEvent(eventId, onSuccess, onFailure)
    }
}
