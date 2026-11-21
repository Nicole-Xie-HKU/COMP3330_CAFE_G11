package com.group11.cafe.viewmodel

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

    fun getEventsForOrganizer(
        organizerId: String,
        onSuccess: (List<Event>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.getEventsForOrganizer(
            organizerId,
            onSuccess,
            onFailure
        )
    }

    fun getAllEvents(
        onSuccess: (List<Event>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getAllEvents(
            onSuccess,
            onFailure
        )
    }

    fun getEventById(
        eventId: String,
        onSuccess: (Event) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        repository.getEventById(
            eventId,
            onSuccess,
            onFailure
        )
    }

}