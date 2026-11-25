package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateSessionScreen(
    screenTitle: String = "Create Session",
    buttonText: String = "Create Session",
    initialTitle: String = "",
    initialLocation: String = "",
    initialStartTime: String = "",
    initialEndTime: String = "",
    initialFixedTime: Boolean = false,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onSubmit: (
        String,
        String,
        String,
        String,
        Boolean
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

    var fixedTime by remember {
        mutableStateOf(initialFixedTime)
    }

    var showStartPicker by remember {
        mutableStateOf(false)
    }

    var showEndPicker by remember {
        mutableStateOf(false)
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
            },
            modifier = Modifier.fillMaxWidth()
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
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                showStartPicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (startTime.isBlank()) {
                    "Choose start time"
                } else {
                    startTime
                }
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                showEndPicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (endTime.isBlank()) {
                    "Choose end time"
                } else {
                    endTime
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = fixedTime,
                onCheckedChange = {
                    fixedTime = it
                }
            )

            Text("Fixed time (never shifted)")
        }

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
                    endTime,
                    fixedTime
                )
            }
        ) {
            Text(
                if (isLoading) "Saving..." else buttonText
            )
        }
    }

    if (showStartPicker) {
        SessionTimePickerDialog(
            initialTime = startTime,
            onConfirm = { chosen ->
                startTime = chosen
                showStartPicker = false
            },
            onDismiss = {
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        SessionTimePickerDialog(
            initialTime = endTime,
            onConfirm = { chosen ->
                endTime = chosen
                showEndPicker = false
            },
            onDismiss = {
                showEndPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTimePickerDialog(
    initialTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (hour, minute) = clockParts(initialTime)
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        formatClock(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

private fun clockParts(value: String): Pair<Int, Int> {
    return try {
        val time = LocalTime.parse(
            value.trim(),
            DateTimeFormatter.ofPattern("H:mm")
        )
        time.hour to time.minute
    } catch (e: Exception) {
        0 to 0
    }
}

private fun formatClock(hour: Int, minute: Int): String {
    return LocalTime.of(hour, minute)
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}
