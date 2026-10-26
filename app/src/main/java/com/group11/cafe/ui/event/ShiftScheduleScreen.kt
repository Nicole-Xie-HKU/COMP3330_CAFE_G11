package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShiftScheduleScreen(
    onShift15: () -> Unit,
    onShift30: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Shift Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onShift15
        ) {
            Text("+15 Minutes")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onShift30
        ) {
            Text("+30 Minutes")
        }
    }
}