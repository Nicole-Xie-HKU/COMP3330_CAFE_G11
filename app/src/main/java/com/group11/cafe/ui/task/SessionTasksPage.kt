package com.group11.cafe.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.model.Task
import com.group11.cafe.data.model.User
import com.group11.cafe.utils.TimeUtils
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.OrganizationViewModel
import com.group11.cafe.viewmodel.SessionViewModel
import com.group11.cafe.viewmodel.TaskViewModel
import java.util.UUID

@Composable
fun SessionTasksPage(
    sessionId: String,
    canEdit: Boolean
) {
    val sessionViewModel: SessionViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val organizationViewModel: OrganizationViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var session by remember { mutableStateOf(Session()) }
    var event by remember { mutableStateOf(Event()) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var crew by remember { mutableStateOf<List<User>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }

    var title by remember { mutableStateOf("") }
    var minutesBefore by remember { mutableStateOf("") }
    var selectedCrew by remember { mutableStateOf<User?>(null) }

    fun showError(exception: Exception) {
        message = exception.message ?: "Something went wrong."
    }

    fun loadTasks() {
        taskViewModel.getTasksForSession(
            sessionId,
            onSuccess = { tasks = it.sortedBy { task -> task.offsetMinutes } },
            onFailure = { showError(it) }
        )
    }

    fun addTask() {
        val member = selectedCrew
        val minutes = minutesBefore.trim().toIntOrNull()
        val start = TimeUtils.toMinutes(session.startTime)

        if (start == null || event.id.isBlank()) {
            message = "Still loading."
            return
        }

        if (title.isBlank() || minutes == null || member == null) {
            message = "Enter a title, the minutes and pick a crew member."
            return
        }

        if (start - minutes < 0 || start - minutes >= 24 * 60) {
            message = "The task must be on the event day."
            return
        }

        val task = Task(
            id = UUID.randomUUID().toString(),
            eventId = session.eventId,
            eventName = event.name,
            eventDate = event.date,
            sessionId = sessionId,
            crewId = member.id,
            crewName = member.name,
            title = title.trim(),
            offsetMinutes = -minutes
        )

        taskViewModel.createTask(
            task,
            onSuccess = {
                title = ""
                minutesBefore = ""
                message = "Task added."
                loadTasks()
            },
            onFailure = { showError(it) }
        )
    }

    LaunchedEffect(sessionId) {
        loadTasks()

        sessionViewModel.getSessionById(
            sessionId,
            onSuccess = { loaded ->
                session = loaded
                eventViewModel.getEventById(
                    loaded.eventId,
                    onSuccess = { event = it },
                    onFailure = { showError(it) }
                )
            },
            onFailure = { showError(it) }
        )

        if (canEdit) {
            organizationViewModel.getCrewMembers(
                userId,
                onSuccess = { crew = it },
                onFailure = { showError(it) }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("${session.title}, starts ${session.startTime}")

        Text("${tasks.count { it.done }} of ${tasks.size} done")

        if (message != null) {
            Text(message ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEach { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(task.title)
                    Text("${TimeUtils.addMinutes(session.startTime, task.offsetMinutes.toLong())}, ${task.crewName}")
                    Text(if (task.done) "Done" else "Not done")

                    if (canEdit) {
                        Button(
                            onClick = {
                                taskViewModel.deleteTask(
                                    task.id,
                                    onSuccess = { loadTasks() },
                                    onFailure = { showError(it) }
                                )
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

        if (canEdit) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add Task",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task") }
            )

            OutlinedTextField(
                value = minutesBefore,
                onValueChange = { minutesBefore = it },
                label = { Text("Minutes before start") }
            )

            if (crew.isEmpty()) {
                Text("No crew members have joined yet.")
            }

            crew.forEach { member ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCrew?.id == member.id,
                        onClick = { selectedCrew = member }
                    )
                    Text(member.name)
                }
            }

            Button(onClick = { addTask() }) {
                Text("Add Task")
            }
        }
    }
}
