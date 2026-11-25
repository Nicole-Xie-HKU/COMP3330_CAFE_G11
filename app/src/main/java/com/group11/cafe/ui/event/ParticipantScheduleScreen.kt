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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session

@Composable
fun ParticipantScheduleScreen(
    event: Event,
    sessions: List<Session>,
    isLoading: Boolean,
    errorMessage: String?,
    onAnnouncementsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSessionClick: (Session) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Event Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("${event.name} ${event.date}")

        Button(onClick = onAnnouncementsClick) {
            Text("Announcements")
        }

        Button(onClick = onHelpClick) {
            Text("Need Help")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        } else if (sessions.isEmpty()) {
            Text("No sessions have been published yet.")
        } else {
            SessionListScreen(
                sessions = sessions,
                eventDate = event.date,
                onSessionClick = onSessionClick
            )
        }
    }
}
