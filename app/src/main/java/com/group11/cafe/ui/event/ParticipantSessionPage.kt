package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.data.model.Session
import com.group11.cafe.utils.SessionIntents
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.SessionViewModel

@Composable
fun ParticipantSessionPage(
    sessionId: String
) {
    val sessionViewModel: SessionViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val context = LocalContext.current
    var session by remember { mutableStateOf<Session?>(null) }
    var eventDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(sessionId) {
        var fetchedEventId: String? = null
        val listener = sessionViewModel.listenToSession(
            sessionId,
            onChange = { updated ->
                session = updated
                if (updated == null) {
                    errorMessage = "Session not found."
                } else {
                    errorMessage = null
                    if (fetchedEventId != updated.eventId) {
                        eventDate = ""
                        fetchedEventId = updated.eventId
                        eventViewModel.getEventById(
                            updated.eventId,
                            onSuccess = { eventDate = it.date },
                            onFailure = { fetchedEventId = null }
                        )
                    }
                }
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load the session."
            }
        )

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val current = session

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        } else if (current == null) {
            CircularProgressIndicator()
        } else {
            Text(
                text = current.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Location: ${current.location}")
            Text("Time: ${current.startTime} - ${current.endTime}")

            if (current.movedMinutes > 0) {
                Text(
                    text = "Moved ${current.movedMinutes} min later",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (current.movedMinutes < 0) {
                Text(
                    text = "Moved ${-current.movedMinutes} min earlier",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { SessionIntents.openDirections(context, current) }) {
                Text("Directions")
            }

            Button(
                onClick = { SessionIntents.addToCalendar(context, current, eventDate) },
                enabled = eventDate.isNotBlank()
            ) {
                Text("Add to Calendar")
            }

            Button(onClick = { SessionIntents.share(context, current) }) {
                Text("Share")
            }
        }
    }
}
