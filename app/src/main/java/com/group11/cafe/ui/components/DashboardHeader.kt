package com.group11.cafe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.utils.GuestUtils
import com.group11.cafe.viewmodel.DashboardViewModel

@Composable
fun DashboardHeader(
    fallbackRole: String
) {
    val dashboardViewModel: DashboardViewModel = viewModel()
    var name by remember { mutableStateOf("there") }
    var role by remember { mutableStateOf(fallbackRole) }
    var profileError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (GuestUtils.isGuest()) {
            return@LaunchedEffect
        }

        dashboardViewModel.loadCurrentUser(
            onSuccess = { user ->
                if (user.name.isNotBlank()) {
                    name = user.name
                }
                if (user.role.isNotBlank()) {
                    role = user.role
                }
            },
            onFailure = {
                profileError = it.message ?: "Profile unavailable."
            }
        )
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hi, $name",
                style = MaterialTheme.typography.headlineMedium
            )

            SuggestionChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(
                        text = role,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }

        if (profileError != null) {
            Text(
                text = profileError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
