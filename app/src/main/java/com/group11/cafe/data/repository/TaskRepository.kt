package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.Task

class TaskRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun createTask(
        task: Task,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getTasksForSession(
        sessionId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .whereEqualTo("sessionId", sessionId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(Task::class.java))
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getTasksForCrew(
        crewId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .whereEqualTo("crewId", crewId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(Task::class.java))
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun setDone(
        taskId: String,
        done: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .document(taskId)
            .update("done", done)
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
            .collection("tasks")
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

    fun deleteTask(
        taskId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}
