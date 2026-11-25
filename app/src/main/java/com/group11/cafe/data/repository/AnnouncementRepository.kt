package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.Announcement

class AnnouncementRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun post(
        announcement: Announcement,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("announcements")
            .document(announcement.id)
            .set(announcement)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun listen(
        eventId: String,
        onChange: (List<Announcement>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore
            .collection("announcements")
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    onFailure(error)
                } else if (documents != null) {
                    onChange(documents.toObjects(Announcement::class.java))
                }
            }
    }
}
