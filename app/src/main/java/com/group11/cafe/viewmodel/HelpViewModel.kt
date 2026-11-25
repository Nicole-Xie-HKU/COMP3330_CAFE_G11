package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.HelpRequest
import com.group11.cafe.data.repository.HelpRepository

class HelpViewModel : ViewModel() {

    private val repository = HelpRepository()

    fun send(
        request: HelpRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.send(request, onSuccess, onFailure)
    }

    fun listen(
        eventId: String,
        onChange: (List<HelpRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listen(eventId, onChange, onFailure)
    }

    fun claim(
        requestId: String,
        crewName: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.claim(requestId, crewName, onSuccess, onFailure)
    }

    fun finish(
        requestId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.finish(requestId, onSuccess, onFailure)
    }
}
