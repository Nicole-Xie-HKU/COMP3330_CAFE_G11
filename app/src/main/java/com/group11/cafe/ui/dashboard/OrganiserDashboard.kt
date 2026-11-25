package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.ui.components.DashboardHeader

@Composable
fun OrganizerDashboard(
    onCreateEventClick: () -> Unit,
    onViewEventsClick: () -> Unit,
    onManageTasksClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        DashboardHeader(
            fallbackRole = "Organizer"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCreateEventClick
        ) {
            Text("Create Event")
        }

        Button(onClick = {}) {
            Text("Manage Sessions")
        }

        Button(onClick = onManageTasksClick) {
            Text("Manage Tasks")
        }

        Button(
            onClick = onViewEventsClick
        ) {
            Text("My Events")
        }

    }
}