package com.group11.cafe.data.repository


import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.AttendanceRecord

class AttendanceRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun checkIn(
        eventId: String,
        sessionId: String,
        participantId: String,
        participantName: String = "",
        checkedInBy: String = "",
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val recordId = "${eventId}_${sessionId}_${participantId}"

        val attendanceRef = firestore
            .collection("attendance")
            .document(recordId)

        firestore.runTransaction { transaction ->

            val existingRecord = transaction.get(attendanceRef)

            if (existingRecord.exists()) {
                false
            } else {
                val attendance = hashMapOf(
                    "id" to recordId,
                    "eventId" to eventId,
                    "sessionId" to sessionId,
                    "participantId" to participantId,
                    "participantName" to participantName,
                    "checkedInBy" to checkedInBy,
                    "checkedInAt" to FieldValue.serverTimestamp()
                )

                transaction.set(attendanceRef, attendance)
                true
            }

        }.addOnSuccessListener { isNewCheckIn ->
            onSuccess(isNewCheckIn)
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun getAttendanceForSession(
        eventId: String,
        sessionId: String,
        onSuccess: (List<AttendanceRecord>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("attendance")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("sessionId", sessionId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(
                    documents.toObjects(AttendanceRecord::class.java)
                )
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteForEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("attendance")
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

    fun getAttendanceForEvent(
        eventId: String,
        onSuccess: (List<AttendanceRecord>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("attendance")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(
                    documents.toObjects(AttendanceRecord::class.java)
                )
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
