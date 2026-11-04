<<<<<<< HEAD
=======

>>>>>>> origin/hadi/attendance-qr-scanner
package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
<<<<<<< HEAD
fun CrewDashboard() {
=======
fun CrewDashboard(
    onAttendanceScannerClick: () -> Unit
) {
>>>>>>> origin/hadi/attendance-qr-scanner

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            "Crew Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}) {
            Text("My Tasks")
        }

<<<<<<< HEAD
        Button(onClick = {}) {
            Text("Attendance Scanner")
        }
    }
}
=======
        Button(
            onClick = onAttendanceScannerClick
        ) {
            Text("Attendance Scanner")
        }
    }
}
>>>>>>> origin/hadi/attendance-qr-scanner
