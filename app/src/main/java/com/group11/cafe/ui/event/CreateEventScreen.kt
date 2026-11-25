package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateEventScreen(
    errorMessage: String? = null,
    onCreateEvent: (
        String,
        String,
        String
    ) -> Unit
) {

    var eventName by remember {
        mutableStateOf("")
    }

    var eventDescription by remember {
        mutableStateOf("")
    }

    var eventDate by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            "Create Event",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = eventName,
            onValueChange = {
                eventName = it
            },
            label = {
                Text("Event Name")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = eventDescription,
            onValueChange = {
                eventDescription = it
            },
            label = {
                Text("Description")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = eventDate,
            onValueChange = {
                eventDate = it
            },
            label = {
                Text("Date (e.g. 2026-11-30)")
            }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onCreateEvent(
                    eventName,
                    eventDescription,
                    eventDate
                )
            }
        ) {
            Text("Create Event")
        }
    }
}