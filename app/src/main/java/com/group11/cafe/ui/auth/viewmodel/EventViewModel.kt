package com.group11.cafe.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.repository.EventRepository

class EventViewModel : ViewModel() {

    private val repository =
        EventRepository()

    fun createEvent(
        event: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.createEvent(
            event,
            onSuccess,
            onFailure
        )
    }
}