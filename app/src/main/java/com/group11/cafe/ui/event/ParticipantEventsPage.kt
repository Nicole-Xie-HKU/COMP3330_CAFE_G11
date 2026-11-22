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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.group11.cafe.data.model.Event
import com.group11.cafe.viewmodel.EventViewModel

@Composable
fun ParticipantEventsPage(
    onEventClick: (Event) -> Unit
) {
    val eventViewModel: EventViewModel = viewModel()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var lastDocument by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var hasNextPage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadPage() {
        isLoading = true
        errorMessage = null

        eventViewModel.getEventsPage(
            lastDocument,
            onSuccess = { newEvents, newLastDocument, morePages ->
                events = events + newEvents
                lastDocument = newLastDocument
                hasNextPage = morePages
                isLoading = false
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load events."
                isLoading = false
            }
        )
    }

    LaunchedEffect(Unit) {
        loadPage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Events",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty() && isLoading) {
            CircularProgressIndicator()
        } else if (events.isEmpty() && errorMessage != null) {
            Text(errorMessage ?: "")
        } else if (events.isEmpty()) {
            Text("No events are available yet.")
        } else {
            EventListScreen(
                events = events,
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f)
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else if (hasNextPage) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { loadPage() }) {
                    Text("Load Next 10")
                }
            }
        }
    }
}
