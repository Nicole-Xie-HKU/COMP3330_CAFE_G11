package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.Announcement
import com.group11.cafe.data.repository.AnnouncementRepository

class AnnouncementViewModel : ViewModel() {

    private val repository = AnnouncementRepository()

    fun post(
        announcement: Announcement,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.post(announcement, onSuccess, onFailure)
    }

    fun listen(
        eventId: String,
        onChange: (List<Announcement>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return repository.listen(eventId, onChange, onFailure)
    }
}
