package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
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

    fun getEventsForOrganizer(
        organizerId: String,
        onSuccess: (List<Event>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("events")
            .whereEqualTo(
                "organizerId",
                organizerId
            )
            .get()
            .addOnSuccessListener { documents ->

                val events =
                    documents.toObjects(Event::class.java)

                onSuccess(events)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getAllEvents(
        onSuccess: (List<Event>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("events")
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(
                    documents.toObjects(Event::class.java)
                )
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getEventsPage(
        lastDocument: DocumentSnapshot?,
        onSuccess: (List<Event>, DocumentSnapshot?, Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = firestore
            .collection("events")
            .orderBy("name", Query.Direction.ASCENDING)
            .limit(10)

        if (lastDocument != null) {
            query = query.startAfter(lastDocument)
        }

        query
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(
                    documents.toObjects(Event::class.java),
                    documents.lastOrNull(),
                    documents.size() == 10
                )
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getEventById(
        eventId: String,
        onSuccess: (Event) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener {

                val event =
                    it.toObject(Event::class.java)

                if (event != null) {
                    onSuccess(event)
                } else {
                    onFailure(Exception("Event not found."))
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

}