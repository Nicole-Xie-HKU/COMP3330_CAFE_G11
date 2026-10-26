package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Event

@Composable
fun EventDetailsScreen(
    event: Event,
    onViewSessionsClick: () -> Unit
) {

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

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onViewSessionsClick
        ) {
            Text("Sessions")
        }
    }
}