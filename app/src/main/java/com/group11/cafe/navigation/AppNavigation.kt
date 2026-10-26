package com.group11.cafe.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.auth.RegisterScreen
import com.group11.cafe.viewmodel.RegisterViewModel
import com.group11.cafe.viewmodel.LoginViewModel
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.ui.dashboard.DashboardRouter
import com.group11.cafe.ui.dashboard.OrganizerDashboard
import com.group11.cafe.ui.dashboard.CrewDashboard
import com.group11.cafe.ui.dashboard.ParticipantDashboard
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import com.group11.cafe.data.model.Event
import com.group11.cafe.ui.event.CreateEventScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect

import com.group11.cafe.ui.event.EventListScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import com.group11.cafe.data.model.Session

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.navArgument
import androidx.navigation.NavType

import com.group11.cafe.ui.event.EventDetailsScreen
import com.group11.cafe.viewmodel.SessionViewModel
import com.group11.cafe.ui.event.SessionListScreen
import com.group11.cafe.ui.event.CreateSessionScreen

import com.group11.cafe.ui.event.SessionDetailsScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect

import com.group11.cafe.ui.event.ShiftScheduleScreen
import com.group11.cafe.utils.TimeUtils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



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
                },

                onViewEventsClick = {
                    navController.navigate(
                        Screen.EventList.route
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
        composable(
            Screen.EventList.route
        ) {

            val viewModel =
                viewModel<EventViewModel>()

            var events by remember {
                mutableStateOf<List<Event>>(
                    emptyList()
                )
            }

            val uid =
                FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?.uid

            LaunchedEffect(Unit) {

                if (uid != null) {

                    viewModel.getEventsForOrganizer(
                        uid,

                        onSuccess = {
                            events = it
                        },

                        onFailure = {
                            println(it.message)
                        }
                    )
                }
            }

            EventListScreen(
                events = events,

                onEventClick = { event ->

                    navController.navigate(
                        Screen.EventDetails.createRoute(
                            event.id
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            )
        ) {

            val eventId =
                it.arguments?.getString("eventId")
                    ?: return@composable

            val eventViewModel =
                viewModel<EventViewModel>()

            var event by remember {
                mutableStateOf<Event?>(null)
            }

            LaunchedEffect(Unit) {

                eventViewModel.getEventById(
                    eventId,

                    onSuccess = {
                        event = it
                    },

                    onFailure = {
                        println(it.message)
                    }
                )
            }

            event?.let { currentEvent ->

                EventDetailsScreen(

                    event = currentEvent,

                    onViewSessionsClick = {

                        navController.navigate(
                            Screen.SessionList.createRoute(
                                currentEvent.id
                            )
                        )
                    }
                )
            }
        }

        composable(
            route = Screen.SessionList.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            )
        ) {

            val eventId =
                it.arguments?.getString("eventId")
                    ?: return@composable

            val sessionViewModel =
                viewModel<SessionViewModel>()

            var sessions by remember {
                mutableStateOf<List<Session>>(
                    emptyList()
                )
            }

            LaunchedEffect(Unit) {

                sessionViewModel.getSessionsForEvent(
                    eventId,

                    onSuccess = {
                        sessions = it
                    },

                    onFailure = {
                        println(it.message)
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(40.dp)
                )

                Button(
                    onClick = {

                        navController.navigate(
                            Screen.CreateSession.createRoute(
                                eventId
                            )
                        )
                    }
                ) {
                    Text("Add Session")
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                SessionListScreen(
                    sessions = sessions,
                    onSessionClick = { session ->

                        navController.navigate(
                            Screen.SessionDetails.createRoute(
                                session.id
                            )
                        )
                    }
                )
            }
        }

        composable(
            route = Screen.CreateSession.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            )
        ) {

            val eventId =
                it.arguments?.getString("eventId")
                    ?: return@composable

            val sessionViewModel =
                viewModel<SessionViewModel>()

            CreateSessionScreen(

                onCreateSession = {
                        title,
                        location,
                        startTime,
                        endTime ->

                    val session =
                        Session(
                            id = UUID.randomUUID().toString(),
                            eventId = eventId,
                            title = title,
                            location = location,
                            startTime = startTime,
                            endTime = endTime
                        )

                    sessionViewModel.createSession(
                        session,

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

        composable(
            route = Screen.SessionDetails.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                }
            )
        ) {

            val sessionId =
                it.arguments?.getString("sessionId")
                    ?: return@composable

            val sessionViewModel =
                viewModel<SessionViewModel>()

            var session by remember {
                mutableStateOf<Session?>(null)
            }

            LaunchedEffect(Unit) {

                sessionViewModel.getSessionById(
                    sessionId,

                    onSuccess = {
                        session = it
                    },

                    onFailure = {
                        println(it.message)
                    }
                )
            }

            session?.let { currentSession ->

                val eventId = currentSession.eventId

                SessionDetailsScreen(

                    session = currentSession,

                    onEditClick = {

                    },

                    onDeleteClick = {

                        sessionViewModel.deleteSession(
                            currentSession.id,

                            onSuccess = {
                                navController.popBackStack()
                            },

                            onFailure = {
                                println(it.message)
                            }
                        )
                    },

                    onShiftClick = {

                        navController.navigate(
                            Screen.ShiftSchedule.createRoute(
                                currentSession.id
                            )
                        )
                    }
                )
            }
        }

        composable(
            route = Screen.ShiftSchedule.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                }
            )
        ) {

            val sessionId =
                it.arguments?.getString("sessionId")
                    ?: return@composable

            val sessionViewModel =
                viewModel<SessionViewModel>()

            var currentSession by remember {
                mutableStateOf<Session?>(null)
            }

            LaunchedEffect(Unit) {

                sessionViewModel.getSessionById(
                    sessionId,

                    onSuccess = {
                        println("SESSION FOUND: ${it.title}")
                        currentSession = it
                    },

                    onFailure = {
                        println("SESSION LOAD FAILED: ${it.message}")
                        println(it.message)
                    }
                )
            }

            currentSession?.let { session ->
                if (currentSession == null) {

                    Text("Loading session...")

                } else {

                    val session = currentSession!!

                    ShiftScheduleScreen(

                        onShift15 = {

                            sessionViewModel.getAllSessionsForEvent(
                                session.eventId,

                                onSuccess = { sessions ->

                                    val updatedSessions =
                                        sessions.map {

                                            it.copy(
                                                startTime =
                                                    TimeUtils.addMinutes(
                                                        it.startTime,
                                                        15
                                                    ),

                                                endTime =
                                                    TimeUtils.addMinutes(
                                                        it.endTime,
                                                        15
                                                    )
                                            )
                                        }

                                    sessionViewModel.updateSessions(
                                        updatedSessions,

                                        onSuccess = {
                                            navController.popBackStack()
                                        },

                                        onFailure = {
                                            println(it.message)
                                        }
                                    )
                                },

                                onFailure = {
                                    println(it.message)
                                }
                            )
                        },

                        onShift30 = {

                        }
                    )
                }
            }
        }
    }
}

