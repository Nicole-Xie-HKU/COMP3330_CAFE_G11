package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.model.Task
import com.group11.cafe.data.repository.TaskRepository

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    fun createTask(
        task: Task,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.createTask(task, onSuccess, onFailure)
    }

    fun getTasksForOrganizer(
        organizerId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getTasksForOrganizer(organizerId, onSuccess, onFailure)
    }

    fun getTasksForSession(
        sessionId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getTasksForSession(sessionId, onSuccess, onFailure)
    }

    fun getTasksForCrew(
        crewId: String,
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getTasksForCrew(crewId, onSuccess, onFailure)
    }

    fun setTaskCompleted(
        taskId: String,
        completed: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.setTaskCompleted(taskId, completed, onSuccess, onFailure)
    }

    fun setDone(
        taskId: String,
        done: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.setDone(taskId, done, onSuccess, onFailure)
    }

    fun deleteTask(
        taskId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.deleteTask(taskId, onSuccess, onFailure)
    }
}
