package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "CAFE Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text("Upcoming Event")

                Spacer(modifier = Modifier.height(8.dp))

                Text("No events yet")
            }
        }
    }
}