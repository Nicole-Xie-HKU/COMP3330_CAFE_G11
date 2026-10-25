package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OrganizerDashboard(
    onCreateEventClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            "Organizer Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCreateEventClick
        ) {
            Text("Create Event")
        }

        Button(onClick = {}) {
            Text("Manage Sessions")
        }

        Button(onClick = {}) {
            Text("Manage Crew")
        }
    }
}