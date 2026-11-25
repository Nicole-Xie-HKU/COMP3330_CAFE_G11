package com.group11.cafe.ui.help

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.HelpRequest
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.HelpViewModel
import java.util.UUID

@Composable
fun AskHelpPage(
    eventId: String
) {
    val helpViewModel: HelpViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val requesterId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var name by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf<List<HelpRequest>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        dashboardViewModel.loadCurrentUser(
            onSuccess = { name = it.name },
            onFailure = { message = it.message }
        )
    }

    DisposableEffect(eventId) {
        val listener = helpViewModel.listen(
            eventId,
            onChange = {
                requests = it
                    .filter { request -> request.requesterId == requesterId }
                    .sortedByDescending { request -> request.createdAt }
            },
            onFailure = { message = it.message }
        )

        onDispose {
            listener.remove()
        }
    }

    fun send() {
        if (place.isBlank()) {
            message = "Enter where you are."
            return
        }

        helpViewModel.send(
            HelpRequest(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                requesterId = requesterId,
                requesterName = name,
                place = place.trim(),
                note = note.trim(),
                createdAt = System.currentTimeMillis()
            ),
            onSuccess = {
                note = ""
                message = "Help is on the way."
            },
            onFailure = { message = it.message ?: "Could not send." }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Ask for Help",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("Where are you?") }
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("What do you need?") }
        )

        Button(onClick = { send() }) {
            Text("Send")
        }

        if (message != null) {
            Text(message ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(requests) { request ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${request.place} ${request.note}")

                        when (request.status) {
                            "open" -> Text("Waiting for crew")
                            "claimed" -> Text("${request.crewName} is coming")
                            else -> Text("Done")
                        }
                    }
                }
            }
        }
    }
}
