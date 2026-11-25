package com.group11.cafe.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Task
import com.group11.cafe.data.model.User
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.OrganizationViewModel
import com.group11.cafe.viewmodel.TaskViewModel
import java.util.UUID

@Composable
fun TaskManagementScreen(isOrganizer: Boolean) {
    val taskViewModel: TaskViewModel = viewModel()
    val organizationViewModel: OrganizationViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var title by remember { mutableStateOf("") }
    var dueAt by remember { mutableStateOf("") }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoadingEvents by remember { mutableStateOf(true) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var isEventMenuExpanded by remember { mutableStateOf(false) }
    var crewMembers by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedCrew by remember { mutableStateOf<User?>(null) }
    var isCrewMenuExpanded by remember { mutableStateOf(false) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }

    fun loadTasks() {
        if (currentUserId == null) {
            errorMessage = "Please sign in again."
            isLoading = false
            return
        }

        val onSuccess: (List<Task>) -> Unit = {
            tasks = it.sortedBy(Task::dueAt)
            isLoading = false
        }
        val onFailure: (Exception) -> Unit = {
            errorMessage = it.message ?: "Could not load tasks."
            isLoading = false
        }

        if (isOrganizer) {
            taskViewModel.getTasksForOrganizer(currentUserId, onSuccess, onFailure)
        } else {
            taskViewModel.getTasksForCrew(currentUserId, onSuccess, onFailure)
        }
    }

    LaunchedEffect(currentUserId, isOrganizer) {
        loadTasks()
        if (isOrganizer && currentUserId != null) {
            eventViewModel.getEventsForOrganizer(
                organizerId = currentUserId,
                onSuccess = {
                    events = it.sortedBy(Event::name)
                    isLoadingEvents = false
                },
                onFailure = {
                    errorMessage = it.message ?: "Could not load events."
                    isLoadingEvents = false
                }
            )
            organizationViewModel.getCrewMembers(
                organizerId = currentUserId,
                onSuccess = { crewMembers = it },
                onFailure = {
                    errorMessage = it.message ?: "Could not load crew members."
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = if (isOrganizer) "Manage Tasks" else "My Tasks",
            style = MaterialTheme.typography.headlineMedium
        )

        if (isOrganizer) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueAt,
                onValueChange = { dueAt = it },
                label = { Text("Due date/time (e.g. Nov 25, 10:00 AM)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Button(
                    onClick = { isEventMenuExpanded = true },
                    enabled = events.isNotEmpty()
                ) {
                    Text(selectedEvent?.name ?: "Select event")
                }
                DropdownMenu(
                    expanded = isEventMenuExpanded,
                    onDismissRequest = { isEventMenuExpanded = false }
                ) {
                    events.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event.name) },
                            onClick = {
                                selectedEvent = event
                                isEventMenuExpanded = false
                            }
                        )
                    }
                }
            }
            if (!isLoadingEvents && events.isEmpty()) {
                Text("Create an event before adding a task.")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Button(onClick = { isCrewMenuExpanded = true }) {
                    Text(selectedCrew?.name ?: "Assign crew (optional)")
                }
                DropdownMenu(
                    expanded = isCrewMenuExpanded,
                    onDismissRequest = { isCrewMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            selectedCrew = null
                            isCrewMenuExpanded = false
                        }
                    )
                    crewMembers.forEach { crew ->
                        DropdownMenuItem(
                            text = { Text(crew.name) },
                            onClick = {
                                selectedCrew = crew
                                isCrewMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                enabled = !isSaving && events.isNotEmpty(),
                onClick = {
                    val event = selectedEvent
                    if (currentUserId == null) {
                        errorMessage = "Please sign in again."
                    } else if (title.isBlank() || dueAt.isBlank() || event == null) {
                        errorMessage = "Enter a task title and due date/time, and select an event."
                    } else {
                        isSaving = true
                        errorMessage = null
                        notice = null
                        val task = Task(
                            id = UUID.randomUUID().toString(),
                            title = title.trim(),
                            dueAt = dueAt.trim(),
                            organizerId = currentUserId,
                            eventId = event.id,
                            eventName = event.name,
                            assignedCrewId = selectedCrew?.id.orEmpty(),
                            assignedCrewName = selectedCrew?.name.orEmpty()
                        )
                        taskViewModel.createTask(
                            task = task,
                            onSuccess = {
                                title = ""
                                dueAt = ""
                                selectedEvent = null
                                selectedCrew = null
                                isSaving = false
                                notice = "Task created."
                                loadTasks()
                            },
                            onFailure = {
                                errorMessage = it.message ?: "Could not create task."
                                isSaving = false
                            }
                        )
                    }
                }
            ) {
                Text(if (isSaving) "Creating..." else "Create Task")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        notice?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (isOrganizer) "Team tasks" else "Assigned to you",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> CircularProgressIndicator()
            tasks.isEmpty() -> Text(
                if (isOrganizer) "No tasks created yet." else "No tasks assigned to you."
            )
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = Task::id) { task ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(task.title, style = MaterialTheme.typography.titleMedium)
                            Text("Event: ${task.eventName.ifBlank { "Not linked" }}")
                            Text("Due: ${task.dueAt}")
                            if (isOrganizer) {
                                Text(
                                    "Assigned to: ${
                                        task.assignedCrewName.ifBlank { "Unassigned" }
                                    }"
                                )
                            }
                            Text(if (task.completed) "Completed" else "Open")
                            if (!isOrganizer && !task.completed) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        taskViewModel.setTaskCompleted(
                                            taskId = task.id,
                                            completed = true,
                                            onSuccess = {
                                                tasks = tasks.map {
                                                    if (it.id == task.id) {
                                                        it.copy(completed = true)
                                                    } else {
                                                        it
                                                    }
                                                }
                                                errorMessage = null
                                            },
                                            onFailure = {
                                                errorMessage =
                                                    it.message ?: "Could not complete task."
                                            }
                                        )
                                    }
                                ) {
                                    Text("Mark complete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
