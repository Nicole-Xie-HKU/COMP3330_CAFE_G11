package com.group11.cafe.ui.task

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.model.Task
import com.group11.cafe.utils.TimeUtils
import com.group11.cafe.viewmodel.SessionViewModel
import com.group11.cafe.viewmodel.TaskViewModel

@Composable
fun MyTasksPage() {
    val taskViewModel: TaskViewModel = viewModel()
    val sessionViewModel: SessionViewModel = viewModel()
    val crewId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun loadTasks() {
        taskViewModel.getTasksForCrew(
            crewId,
            onSuccess = { loaded ->
                tasks = loaded
                isLoading = false

                loaded.map { it.eventId }.distinct().forEach { eventId ->
                    sessionViewModel.getSessionsForEvent(
                        eventId,
                        onSuccess = { sessions = sessions + it },
                        onFailure = { errorMessage = it.message }
                    )
                }
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load tasks."
                isLoading = false
            }
        )
    }

    fun timeOf(task: Task): String {
        val session = sessions.find { it.id == task.sessionId } ?: return ""
        return TimeUtils.addMinutes(session.startTime, task.offsetMinutes.toLong())
    }

    fun setDone(task: Task, done: Boolean) {
        taskViewModel.setDone(
            task.id,
            done,
            onSuccess = {
                tasks = tasks.map {
                    if (it.id == task.id) it.copy(done = done) else it
                }
            },
            onFailure = { errorMessage = it.message }
        )
    }

    LaunchedEffect(crewId) {
        loadTasks()
    }

    // refresh the time left twice a minute
    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val ticker = object : Runnable {
            override fun run() {
                now = System.currentTimeMillis()
                handler.postDelayed(this, 30000)
            }
        }
        handler.post(ticker)

        onDispose {
            handler.removeCallbacks(ticker)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (tasks.isEmpty()) {
            Text("You have no tasks yet.")
        } else {
            val sortedTasks = tasks
                .filter { task -> sessions.any { it.id == task.sessionId } }
                .sortedBy { TimeUtils.toMillis(it.eventDate, timeOf(it)) }

            LazyColumn {
                items(sortedTasks) { task ->
                    val session = sessions.find { it.id == task.sessionId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { setDone(task, it) }
                            )

                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("${timeOf(task)} ${task.title}")
                                Text("${task.eventName} ${task.eventDate}")

                                if (!task.done) {
                                    Text(TimeUtils.timeLeft(task.eventDate, timeOf(task), now))
                                }

                                if (session != null) {
                                    Text("${session.title} at ${session.location}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
