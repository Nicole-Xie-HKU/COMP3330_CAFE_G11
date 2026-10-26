package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.Session

class SessionRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun createSession(
        session: Session,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .document(session.id)
            .set(session)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getSessionsForEvent(
        eventId: String,
        onSuccess: (List<Session>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { documents ->

                val sessions =
                    documents.toObjects(Session::class.java)

                onSuccess(sessions)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun updateSession(
        session: Session,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .document(session.id)
            .set(session)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteSession(
        sessionId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .document(sessionId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getSessionById(
        sessionId: String,
        onSuccess: (Session) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .document(sessionId)
            .get()
            .addOnSuccessListener {

                val session =
                    it.toObject(Session::class.java)

                if(session != null) {
                    onSuccess(session)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getAllSessionsForEvent(
        eventId: String,
        onSuccess: (List<Session>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        firestore
            .collection("sessions")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener {

                val sessions =
                    it.toObjects(Session::class.java)

                onSuccess(sessions)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun updateSessions(
        sessions: List<Session>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val batch =
            firestore.batch()

        sessions.forEach { session ->

            val ref =
                firestore
                    .collection("sessions")
                    .document(session.id)

            batch.set(ref, session)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

}