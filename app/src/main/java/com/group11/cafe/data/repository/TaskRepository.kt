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

    fun getTasksForOrganizer(
        organizerId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .whereEqualTo("organizerId", organizerId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(Task::class.java))
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
        val results = mutableMapOf<String, Task>()
        var completedQueries = 0
        var queryFailure: Exception? = null

        fun finishQuery(tasks: List<Task>?, failure: Exception?) {
            tasks?.forEach { task -> results[task.id] = task }
            if (failure != null && queryFailure == null) {
                queryFailure = failure
            }
            completedQueries++
            if (completedQueries == 2) {
                queryFailure?.let(onFailure) ?: onSuccess(results.values.toList())
            }
        }

        listOf("crewId", "assignedCrewId").forEach { field ->
            firestore
                .collection("tasks")
                .whereEqualTo(field, crewId)
                .get()
                .addOnSuccessListener { documents ->
                    finishQuery(documents.toObjects(Task::class.java), null)
                }
                .addOnFailureListener { failure ->
                    finishQuery(null, failure)
                }
        }
    }

    fun setTaskCompleted(
        taskId: String,
        completed: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .document(taskId)
            .update(mapOf("completed" to completed, "done" to completed))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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
            .update(mapOf("done" to done, "completed" to done))
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
