package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.Signup

class SignupRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun signUp(
        signup: Signup,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val id = "${signup.eventId}_${signup.userId}"

        firestore
            .collection("signups")
            .document(id)
            .set(signup.copy(id = id))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun cancel(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("signups")
            .document("${eventId}_${userId}")
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun listenForUser(
        userId: String,
        onChange: (List<Signup>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore
            .collection("signups")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    onFailure(error)
                } else if (documents != null) {
                    onChange(documents.toObjects(Signup::class.java))
                }
            }
    }

    fun listenForEvent(
        eventId: String,
        onChange: (List<Signup>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore
            .collection("signups")
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    onFailure(error)
                } else if (documents != null) {
                    onChange(documents.toObjects(Signup::class.java))
                }
            }
    }

    fun deleteForEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("signups")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                val batch = firestore.batch()
                documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
