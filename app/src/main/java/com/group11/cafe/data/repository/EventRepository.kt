package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.Event

class EventRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun createEvent(
        event: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("events")
            .document(event.id)
            .set(event)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}