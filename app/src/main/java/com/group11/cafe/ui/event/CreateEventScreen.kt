package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateEventScreen(
    onCreateEvent: (
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onCreateEvent(
                    eventName,
                    eventDescription
                )
            }
        ) {
            Text("Create Event")
        }
    }
}