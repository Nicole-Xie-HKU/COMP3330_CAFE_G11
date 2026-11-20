package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateSessionScreen(
    screenTitle: String = "Create Session",
    buttonText: String = "Create Session",
    initialTitle: String = "",
    initialLocation: String = "",
    initialStartTime: String = "",
    initialEndTime: String = "",
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onSubmit: (
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var title by remember {
        mutableStateOf(initialTitle)
    }

    var location by remember {
        mutableStateOf(initialLocation)
    }

    var startTime by remember {
        mutableStateOf(initialStartTime)
    }

    var endTime by remember {
        mutableStateOf(initialEndTime)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            screenTitle,
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Title")
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
            },
            label = {
                Text("Location")
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = startTime,
            onValueChange = {
                startTime = it
            },
            label = {
                Text("Start Time")
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = endTime,
            onValueChange = {
                endTime = it
            },
            label = {
                Text("End Time")
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            enabled = !isLoading,
            onClick = {
                onSubmit(
                    title,
                    location,
                    startTime,
                    endTime
                )
            }
        ) {
            Text(
                if (isLoading) "Saving..." else buttonText
            )
        }
    }
}