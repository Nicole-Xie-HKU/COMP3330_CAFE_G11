package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Session

@Composable
fun SessionDetailsScreen(
    session: Session,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShiftClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = session.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text("Location: ${session.location}")

        Text(
            "Time: ${session.startTime} - ${session.endTime}"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onEditClick
        ) {
            Text("Edit Session")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onDeleteClick
        ) {
            Text("Delete Session")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onShiftClick
        ) {
            Text("Shift Following Sessions")
        }
    }
}