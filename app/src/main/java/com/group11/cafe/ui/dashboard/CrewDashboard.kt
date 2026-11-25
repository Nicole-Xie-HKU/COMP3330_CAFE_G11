package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.ui.components.DashboardHeader

@Composable
fun CrewDashboard(
    onAttendanceScannerClick: () -> Unit
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
            fallbackRole = "Crew"
        )

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = onAttendanceScannerClick
        ) {
            Text("Attendance Scanner")
        }
    }
}
