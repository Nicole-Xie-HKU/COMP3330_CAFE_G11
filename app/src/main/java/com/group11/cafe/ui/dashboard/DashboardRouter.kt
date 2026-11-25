package com.group11.cafe.ui.dashboard

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.group11.cafe.navigation.Screen
import com.group11.cafe.viewmodel.DashboardViewModel


@Composable
fun DashboardRouter(
    navController: NavController
) {

    val viewModel =
        viewModel<DashboardViewModel>()

    LaunchedEffect(Unit) {

        viewModel.loadRole { role ->

            when(role.trim()) {

                "Organizer" -> {
                    navController.navigate(
                        Screen.OrganizerDashboard.route
                    ) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }

                "Crew" -> {
                    navController.navigate(
                        Screen.CrewDashboard.route
                    ) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }

                "Participant" -> {
                    navController.navigate(
                        Screen.ParticipantDashboard.route
                    ) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }

                else -> {
                    navController.navigate(
                        Screen.ParticipantDashboard.route
                    ) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    CircularProgressIndicator()
}