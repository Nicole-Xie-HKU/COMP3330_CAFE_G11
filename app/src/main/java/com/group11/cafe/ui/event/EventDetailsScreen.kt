package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Event

@Composable
fun EventDetailsScreen(
    event: Event,
    canEdit: Boolean,
    errorMessage: String? = null,
    onAnnouncementsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onViewSessionsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = event.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(event.description)

        if (event.date.isNotBlank()) {
            Text("Date: ${event.date}")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onViewSessionsClick
        ) {
            Text("Sessions")
        }

        Button(
            onClick = onAnnouncementsClick
        ) {
            Text("Announcements")
        }

        Button(
            onClick = onHelpClick
        ) {
            Text("Help Requests")
        }

        if (canEdit) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onEditClick) {
                Text("Edit Event")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { showDeleteDialog = true }) {
                Text("Delete Event")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this event?") },
            text = {
                Text(
                    "Sessions, tasks, announcements, help requests, and attendance for this event will be removed."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
