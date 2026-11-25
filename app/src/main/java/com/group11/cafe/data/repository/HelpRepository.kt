package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.group11.cafe.data.model.HelpRequest

class HelpRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun send(
        request: HelpRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("helpRequests")
            .document(request.id)
            .set(request)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteForEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("helpRequests")
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

    fun listen(
        eventId: String,
        onChange: (List<HelpRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore
            .collection("helpRequests")
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    onFailure(error)
                } else if (documents != null) {
                    onChange(documents.toObjects(HelpRequest::class.java))
                }
            }
    }

    fun claim(
        requestId: String,
        crewName: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val request = firestore
            .collection("helpRequests")
            .document(requestId)

        firestore.runTransaction { transaction ->
            if (transaction.get(request).getString("status") == "open") {
                transaction.update(request, "status", "claimed")
                transaction.update(request, "crewName", crewName)
                true
            } else {
                false
            }
        }.addOnSuccessListener {
            onSuccess(it)
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun finish(
        requestId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("helpRequests")
            .document(requestId)
            .update("status", "done")
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}
