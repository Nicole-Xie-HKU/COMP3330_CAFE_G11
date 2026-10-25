package com.group11.cafe.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.auth.RegisterScreen
import com.group11.cafe.viewmodel.RegisterViewModel
import com.group11.cafe.ui.auth.viewmodel.LoginViewModel
import com.group11.cafe.ui.auth.viewmodel.EventViewModel
import com.group11.cafe.ui.dashboard.DashboardRouter
import com.group11.cafe.ui.dashboard.OrganizerDashboard
import com.group11.cafe.ui.dashboard.CrewDashboard
import com.group11.cafe.ui.dashboard.ParticipantDashboard
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import com.group11.cafe.data.model.Event
import com.group11.cafe.ui.event.CreateEventScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            val loginViewModel =
                viewModel<LoginViewModel>()

            LoginScreen(

                onLoginClick = { email, password ->

                    loginViewModel.login(

                        email,
                        password,

                        onSuccess = {
                            navController.navigate(
                                Screen.Dashboard.route
                            )
                        },

                        onFailure = {
                            println(it.message)
                        }
                    )
                },

                onGuestClick = {

                    FirebaseAuth.getInstance().signOut()

                    navController.navigate(
                        Screen.ParticipantDashboard.route
                    )
                },

                onRegisterClick = {
                    navController.navigate(
                        Screen.Register.route
                    )
                }
            )
        }

        composable(Screen.Register.route) {

            val viewModel =
                viewModel<RegisterViewModel>()

            RegisterScreen(
                onRegisterClick = { name, email, password, role ->

                    viewModel.register(
                        name,
                        email,
                        password,
                        role,
                        onSuccess = {

                            navController.navigate(
                                Screen.Dashboard.route
                            )
                        },
                        onFailure = {
                            println(it.message)
                        }
                    )
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardRouter(navController)
        }

        composable(Screen.OrganizerDashboard.route) {
            OrganizerDashboard(
                onCreateEventClick = {
                    navController.navigate(
                        Screen.CreateEvent.route
                    )
                }
            )
        }

        composable(Screen.CrewDashboard.route) {
            CrewDashboard()
        }

        composable(Screen.ParticipantDashboard.route) {
            ParticipantDashboard()
        }
        composable(
            Screen.CreateEvent.route
        ) {

            val eventViewModel =
                viewModel<EventViewModel>()

            CreateEventScreen(
                onCreateEvent = { name, description ->

                    val uid =
                        FirebaseAuth
                            .getInstance()
                            .currentUser
                            ?.uid ?: return@CreateEventScreen

                    val event =
                        Event(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            description = description,
                            organizerId = uid
                        )

                    eventViewModel.createEvent(
                        event,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onFailure = {
                            println(it.message)
                        }
                    )
                }
            )
        }
    }
}
