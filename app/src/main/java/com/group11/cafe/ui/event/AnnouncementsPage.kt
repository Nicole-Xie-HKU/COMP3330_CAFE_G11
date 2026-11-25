package com.group11.cafe.ui.event

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.data.model.Announcement
import com.group11.cafe.viewmodel.AnnouncementViewModel
import java.util.UUID

@Composable
fun AnnouncementsPage(
    eventId: String,
    canPost: Boolean
) {
    val announcementViewModel: AnnouncementViewModel = viewModel()
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var text by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    DisposableEffect(eventId) {
        val listener = announcementViewModel.listen(
            eventId,
            onChange = {
                announcements = it.sortedByDescending { item -> item.createdAt }
            },
            onFailure = { message = it.message }
        )

        onDispose {
            listener.remove()
        }
    }

    fun post() {
        if (text.isBlank()) {
            message = "Write a message first."
            return
        }

        announcementViewModel.post(
            Announcement(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                text = text.trim(),
                createdAt = System.currentTimeMillis()
            ),
            onSuccess = {
                text = ""
                message = "Posted."
            },
            onFailure = { message = it.message ?: "Could not post." }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Announcements",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (canPost) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Message") }
            )

            Button(onClick = { post() }) {
                Text("Post")
            }
        }

        if (message != null) {
            Text(message ?: "")
        }

        if (announcements.isEmpty()) {
            Text("No announcements yet.")
        }

        LazyColumn {
            items(announcements) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = item.text,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
