package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.repository.AnnouncementRepository
import com.group11.cafe.data.repository.AttendanceRepository
import com.group11.cafe.data.repository.EventRepository
import com.group11.cafe.data.repository.HelpRepository
import com.group11.cafe.data.repository.SessionRepository
import com.group11.cafe.data.repository.TaskRepository

class EventViewModel : ViewModel() {

    private val repository =
        EventRepository()

    private val sessionRepository =
        SessionRepository()

    private val taskRepository =
        TaskRepository()

    private val announcementRepository =
        AnnouncementRepository()

    private val helpRepository =
        HelpRepository()

    private val attendanceRepository =
        AttendanceRepository()

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

    fun getEventsForOrganizers(
        organizerIds: List<String>,
        onSuccess: (List<Event>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getEventsForOrganizers(
            organizerIds,
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

    fun getEventsPage(
        lastDocument: DocumentSnapshot?,
        onSuccess: (List<Event>, DocumentSnapshot?, Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getEventsPage(
            lastDocument,
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

    fun listenToEvent(
        eventId: String,
        onChange: (Event?) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listenToEvent(
            eventId,
            onChange,
            onFailure
        )
    }

    fun updateEvent(
        event: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.updateEvent(
            event,
            onSuccess,
            onFailure
        )
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        sessionRepository.deleteForEvent(
            eventId,
            onSuccess = {
                taskRepository.deleteForEvent(
                    eventId,
                    onSuccess = {
                        announcementRepository.deleteForEvent(
                            eventId,
                            onSuccess = {
                                helpRepository.deleteForEvent(
                                    eventId,
                                    onSuccess = {
                                        attendanceRepository.deleteForEvent(
                                            eventId,
                                            onSuccess = {
                                                repository.deleteEvent(
                                                    eventId,
                                                    onSuccess,
                                                    onFailure
                                                )
                                            },
                                            onFailure
                                        )
                                    },
                                    onFailure
                                )
                            },
                            onFailure
                        )
                    },
                    onFailure
                )
            },
            onFailure
        )
    }

}