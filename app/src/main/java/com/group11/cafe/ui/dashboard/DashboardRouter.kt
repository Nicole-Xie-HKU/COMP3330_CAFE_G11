package com.group11.cafe.ui.dashboard

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.group11.cafe.navigation.Screen
import com.group11.cafe.viewmodel.DashboardViewModel

import android.util.Log

@Composable
fun DashboardRouter(
    navController: NavController
) {

    val viewModel =
        viewModel<DashboardViewModel>()

    LaunchedEffect(Unit) {

        viewModel.loadRole { role ->

            Log.d("ROLE_DEBUG", "Role = $role")

            when(role.trim()) {

                "Organizer" -> {
                    navController.navigate(
                        Screen.OrganizerDashboard.route
                    )
                }

                "Crew" -> {
                    navController.navigate(
                        Screen.CrewDashboard.route
                    )
                }

                "Participant" -> {
                    navController.navigate(
                        Screen.ParticipantDashboard.route
                    )
                }

                else -> {
                    Log.d("ROLE_DEBUG", "Unknown role: $role")

                    navController.navigate(
                        Screen.ParticipantDashboard.route
                    )
                }
            }
        }
    }

    CircularProgressIndicator()
}