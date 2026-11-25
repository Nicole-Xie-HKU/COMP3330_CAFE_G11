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

    fun getTasksForCrew(
        crewId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("tasks")
            .whereEqualTo("assignedCrewId", crewId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(Task::class.java))
            }
            .addOnFailureListener {
                onFailure(it)
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
            .update("completed", completed)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}
