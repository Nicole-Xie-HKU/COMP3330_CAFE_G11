
package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Newest") }

    fun loadPage() {
        if (isLoading) return

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

    // Search and sort the events currently loaded.
    val filteredEvents = remember(events, searchQuery, sortOption) {
        val filtered = events.filter { event ->
            event.name.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true)
        }

        when (sortOption) {
            "A-Z" -> filtered.sortedBy {
                it.name.lowercase()
            }

            "Newest" -> filtered.sortedByDescending {
                it.createdAt
            }

            else -> filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Discover Events",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find events and explore what's happening.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search events") },
            placeholder = { Text("Enter an event name...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sorting options
        Text(
            text = "Sort by",
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = sortOption == "Newest",
                onClick = { sortOption = "Newest" },
                label = { Text("Newest") }
            )

            FilterChip(
                selected = sortOption == "A-Z",
                onClick = { sortOption = "A-Z" },
                label = { Text("A-Z") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${filteredEvents.size} events found",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Event results
        if (events.isEmpty() && isLoading) {

            CircularProgressIndicator()

        } else if (events.isEmpty() && errorMessage != null) {

            Text(errorMessage ?: "Something went wrong.")

            Button(onClick = { loadPage() }) {
                Text("Retry")
            }

        } else if (filteredEvents.isEmpty()) {

            Text(
                text = if (events.isEmpty()) {
                    "No events are available yet."
                } else {
                    "No events match your search."
                },
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            EventListScreen(
                events = filteredEvents,
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Load additional events
        if (isLoading && events.isNotEmpty()) {
            CircularProgressIndicator()
        } else if (hasNextPage) {

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { loadPage() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load Next 10 Events")
            }
        }

        if (errorMessage != null && events.isNotEmpty()) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )

            Button(onClick = { loadPage() }) {
                Text("Retry")
            }
        }
    }
}
