package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.data.model.Session
import com.group11.cafe.utils.ShiftUtils
import com.group11.cafe.viewmodel.SessionViewModel

@Composable
fun ShiftSchedulePage(
    sessionId: String
) {
    val sessionViewModel: SessionViewModel = viewModel()
    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
    var startingSession by remember { mutableStateOf<Session?>(null) }
    var minutes by remember { mutableIntStateOf(0) }
    var oldSessions by remember { mutableStateOf<List<Session>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    fun showError(exception: Exception) {
        message = exception.message ?: "Something went wrong."
        isLoading = false
    }

    fun load() {
        isLoading = true
        sessionViewModel.getSessionById(
            sessionId,
            onSuccess = { session ->
                startingSession = session
                sessionViewModel.getAllSessionsForEvent(
                    session.eventId,
                    onSuccess = {
                        sessions = it
                        isLoading = false
                    },
                    onFailure = { showError(it) }
                )
            },
            onFailure = { showError(it) }
        )
    }

    fun save(changed: List<Session>, text: String, undo: List<Session>) {
        isLoading = true
        sessionViewModel.updateSessions(
            changed,
            onSuccess = {
                message = text
                oldSessions = undo
                minutes = 0
                load()
            },
            onFailure = { showError(it) }
        )
    }

    LaunchedEffect(sessionId) {
        load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Shift Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        val start = startingSession

        if (isLoading) {
            CircularProgressIndicator()
        } else if (start != null) {
            Text("From ${start.title} onwards")

            Row {
                listOf(-15, 15, 30).forEach { value ->
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            minutes = value
                            message = null
                        }
                    ) {
                        Text(if (value > 0) "+$value" else "$value")
                    }
                }
            }

            if (minutes != 0) {
                val before = ShiftUtils.sessionsToMove(sessions, start)
                val after = ShiftUtils.move(before, minutes)
                val staying = ShiftUtils.fixedSessionsStaying(sessions, start)

                Spacer(modifier = Modifier.height(16.dp))

                if (before.isEmpty()) {
                    Text("Nothing to move. These sessions are fixed.")
                } else if (ShiftUtils.passesMidnight(before, minutes)) {
                    Text("A session would pass midnight.")
                } else {
                    val clashes = ShiftUtils.clashes(sessions, after)

                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.titleLarge
                    )

                    after.forEach { moved ->
                        val old = before.find { it.id == moved.id }
                        Text("${moved.title}: ${old?.startTime} to ${moved.startTime}")
                    }

                    clashes.forEach {
                        Text(
                            text = "Overlaps with $it.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    staying.forEach {
                        Text("${it.title} stays at ${it.startTime} (fixed)")
                    }

                    if (clashes.isEmpty()) {
                        Button(
                            onClick = {
                                save(after, "Schedule moved.", before)
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }

            if (oldSessions.isNotEmpty()) {
                Button(
                    onClick = {
                        save(oldSessions, "Shift undone.", emptyList())
                    }
                ) {
                    Text("Undo Last Shift")
                }
            }
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message ?: "")
        }
    }
}
