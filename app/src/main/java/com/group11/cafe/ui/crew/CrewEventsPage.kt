package com.group11.cafe.ui.crew

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.ui.event.EventListScreen
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.OrganizationViewModel

@Composable
fun CrewEventsPage(
    onEventClick: (Event) -> Unit
) {
    val organizationViewModel: OrganizationViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val crewId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun showError(exception: Exception) {
        errorMessage = exception.message ?: "Could not load events."
        isLoading = false
    }

    LaunchedEffect(crewId) {
        organizationViewModel.getOrganizersForCrew(
            crewId,
            onSuccess = { organizers ->
                eventViewModel.getEventsForOrganizers(
                    organizers.map { it.id },
                    onSuccess = {
                        events = it
                        isLoading = false
                    },
                    onFailure = { showError(it) }
                )
            },
            onFailure = { showError(it) }
        )
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

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        } else if (events.isEmpty()) {
            Text("No events yet. Join an organizer on the Team tab.")
        } else {
            EventListScreen(
                events = events,
                onEventClick = onEventClick
            )
        }
    }
}
