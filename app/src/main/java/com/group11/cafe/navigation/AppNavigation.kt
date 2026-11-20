package com.group11.cafe.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session
import com.group11.cafe.ui.attendance.AttendanceScanner
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.auth.RegisterScreen
import com.group11.cafe.ui.dashboard.CrewDashboard
import com.group11.cafe.ui.dashboard.DashboardRouter
import com.group11.cafe.ui.dashboard.OrganizerDashboard
import com.group11.cafe.ui.dashboard.ParticipantDashboard
import com.group11.cafe.ui.event.CreateEventScreen
import com.group11.cafe.ui.event.CreateSessionScreen
import com.group11.cafe.ui.event.EventDetailsScreen
import com.group11.cafe.ui.event.EventListScreen
import com.group11.cafe.ui.event.ParticipantScheduleScreen
import com.group11.cafe.ui.event.SessionDetailsScreen
import com.group11.cafe.ui.event.SessionListScreen
import com.group11.cafe.ui.event.ShiftScheduleScreen
import com.group11.cafe.ui.components.AppBackButton
import com.group11.cafe.utils.TimeUtils
import com.group11.cafe.viewmodel.AttendanceViewModel
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.LoginViewModel
import com.group11.cafe.viewmodel.RegisterViewModel
import com.group11.cafe.viewmodel.SessionViewModel
import java.util.UUID

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != null && currentRoute != Screen.Login.route) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        AppBackButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LoginScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoginClick = { email, password ->
                    isLoading = true
                    errorMessage = null

                    loginViewModel.login(
                        email,
                        password,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.Dashboard.route)
                        },
                        onFailure = {
                            isLoading = false
                            errorMessage = it.message ?: "Login failed."
                        }
                    )
                },
                onGuestClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.ParticipantDashboard.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            RegisterScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRegisterClick = { name, email, password, role ->
                    isLoading = true
                    errorMessage = null

                    registerViewModel.register(
                        name,
                        email,
                        password,
                        role,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.Dashboard.route)
                        },
                        onFailure = {
                            isLoading = false
                            errorMessage = it.message ?: "Registration failed."
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
                    navController.navigate(Screen.CreateEvent.route)
                },
                onViewEventsClick = {
                    navController.navigate(Screen.EventList.route)
                }
            )
        }

        composable(Screen.CrewDashboard.route) {
            CrewDashboard(
                onAttendanceScannerClick = {
                    navController.navigate(Screen.AttendanceScanner.route)
                }
            )
        }

        composable(Screen.AttendanceScanner.route) {
            val attendanceViewModel: AttendanceViewModel = viewModel()
            var statusMessage by remember { mutableStateOf<String?>(null) }

            AttendanceScanner(
                statusMessage = statusMessage,
                onQrScanned = { qrValue ->
                    val values = qrValue.split("|", limit = 4)

                    if (values.size < 3 || values.take(3).any { it.isBlank() }) {
                        statusMessage = "Invalid QR code. Check the expected format."
                        return@AttendanceScanner
                    }

                    statusMessage = "Checking participant in..."
                    attendanceViewModel.checkIn(
                        eventId = values[0].trim(),
                        sessionId = values[1].trim(),
                        participantId = values[2].trim(),
                        participantName = values.getOrNull(3)?.trim().orEmpty(),
                        checkedInBy = FirebaseAuth
                            .getInstance()
                            .currentUser
                            ?.uid
                            .orEmpty(),
                        onSuccess = { isNewCheckIn ->
                            statusMessage = if (isNewCheckIn) {
                                "Participant checked in successfully."
                            } else {
                                "This participant is already checked in."
                            }
                        },
                        onFailure = {
                            statusMessage = it.message ?: "Could not check in participant."
                        }
                    )
                }
            )
        }

        composable(Screen.ParticipantDashboard.route) {
            ParticipantDashboard(
                onViewScheduleClick = {
                    navController.navigate(Screen.ParticipantEventList.route)
                }
            )
        }

        composable(Screen.ParticipantEventList.route) {
            val eventViewModel: EventViewModel = viewModel()
            var events by remember { mutableStateOf<List<Event>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                eventViewModel.getAllEvents(
                    onSuccess = {
                        events = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load events."
                        isLoading = false
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Choose an Event",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                } else if (events.isEmpty()) {
                    Text("No events are available yet.")
                } else {
                    EventListScreen(
                        events = events,
                        onEventClick = {
                            navController.navigate(
                                Screen.ParticipantSchedule.createRoute(it.id)
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.ParticipantSchedule.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments
                ?.getString("eventId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(eventId) {
                sessionViewModel.getSessionsForEvent(
                    eventId,
                    onSuccess = {
                        sessions = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the schedule."
                        isLoading = false
                    }
                )
            }

            ParticipantScheduleScreen(
                sessions = sessions,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }

        composable(Screen.CreateEvent.route) {
            val eventViewModel: EventViewModel = viewModel()

            CreateEventScreen(
                onCreateEvent = { name, description ->
                    val organizerId = FirebaseAuth
                        .getInstance()
                        .currentUser
                        ?.uid
                        ?: return@CreateEventScreen

                    val event = Event(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        organizerId = organizerId
                    )

                    eventViewModel.createEvent(
                        event,
                        onSuccess = { navController.popBackStack() },
                        onFailure = { println(it.message) }
                    )
                }
            )
        }

        composable(Screen.EventList.route) {
            val eventViewModel: EventViewModel = viewModel()
            var events by remember { mutableStateOf<List<Event>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            val organizerId = FirebaseAuth.getInstance().currentUser?.uid

            LaunchedEffect(organizerId) {
                if (organizerId == null) {
                    errorMessage = "Please sign in again."
                    isLoading = false
                    return@LaunchedEffect
                }

                eventViewModel.getEventsForOrganizer(
                    organizerId,
                    onSuccess = {
                        events = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load events."
                        isLoading = false
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text("My Events", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                } else {
                    EventListScreen(
                        events = events,
                        onEventClick = {
                            navController.navigate(
                                Screen.EventDetails.createRoute(it.id)
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments
                ?.getString("eventId")
                ?: return@composable
            val eventViewModel: EventViewModel = viewModel()
            var event by remember { mutableStateOf<Event?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(eventId) {
                eventViewModel.getEventById(
                    eventId,
                    onSuccess = { event = it },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the event."
                    }
                )
            }

            if (event == null && errorMessage == null) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else {
                event?.let { currentEvent ->
                    EventDetailsScreen(
                        event = currentEvent,
                        onViewSessionsClick = {
                            navController.navigate(
                                Screen.SessionList.createRoute(currentEvent.id)
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.SessionList.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments
                ?.getString("eventId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(eventId) {
                sessionViewModel.getSessionsForEvent(
                    eventId,
                    onSuccess = {
                        sessions = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load sessions."
                        isLoading = false
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(
                            Screen.CreateSession.createRoute(eventId)
                        )
                    }
                ) {
                    Text("Add Session")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                } else if (sessions.isEmpty()) {
                    Text("No sessions have been created yet.")
                } else {
                    SessionListScreen(
                        sessions = sessions,
                        onSessionClick = {
                            navController.navigate(
                                Screen.SessionDetails.createRoute(it.id)
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.CreateSession.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments
                ?.getString("eventId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            CreateSessionScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onSubmit = { title, location, startTime, endTime ->
                    if (
                        title.isBlank() ||
                        location.isBlank() ||
                        startTime.isBlank() ||
                        endTime.isBlank()
                    ) {
                        errorMessage = "Complete all session fields."
                        return@CreateSessionScreen
                    }

                    isLoading = true
                    errorMessage = null
                    val session = Session(
                        id = UUID.randomUUID().toString(),
                        eventId = eventId,
                        title = title.trim(),
                        location = location.trim(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim()
                    )

                    sessionViewModel.createSession(
                        session,
                        onSuccess = {
                            isLoading = false
                            navController.popBackStack()
                        },
                        onFailure = {
                            isLoading = false
                            errorMessage = it.message ?: "Could not create the session."
                        }
                    )
                }
            )
        }

        composable(
            route = Screen.EditSession.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString("sessionId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var session by remember { mutableStateOf<Session?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(sessionId) {
                sessionViewModel.getSessionById(
                    sessionId,
                    onSuccess = {
                        session = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the session."
                        isLoading = false
                    }
                )
            }

            if (session == null && errorMessage == null) {
                CircularProgressIndicator()
            } else {
                val currentSession = session

                if (currentSession == null) {
                    Text(
                        text = errorMessage ?: "Session not found.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    CreateSessionScreen(
                        screenTitle = "Edit Session",
                        buttonText = "Save Changes",
                        initialTitle = currentSession.title,
                        initialLocation = currentSession.location,
                        initialStartTime = currentSession.startTime,
                        initialEndTime = currentSession.endTime,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onSubmit = { title, location, startTime, endTime ->
                            if (
                                title.isBlank() ||
                                location.isBlank() ||
                                startTime.isBlank() ||
                                endTime.isBlank()
                            ) {
                                errorMessage = "Complete all session fields."
                                return@CreateSessionScreen
                            }

                            isLoading = true
                            errorMessage = null
                            sessionViewModel.updateSession(
                                currentSession.copy(
                                    title = title.trim(),
                                    location = location.trim(),
                                    startTime = startTime.trim(),
                                    endTime = endTime.trim()
                                ),
                                onSuccess = {
                                    isLoading = false
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    isLoading = false
                                    errorMessage =
                                        it.message ?: "Could not save the session."
                                }
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.SessionDetails.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString("sessionId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var session by remember { mutableStateOf<Session?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(sessionId) {
                sessionViewModel.getSessionById(
                    sessionId,
                    onSuccess = { session = it },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the session."
                    }
                )
            }

            if (session == null && errorMessage == null) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else {
                session?.let { currentSession ->
                    SessionDetailsScreen(
                        session = currentSession,
                        onEditClick = {
                            navController.navigate(
                                Screen.EditSession.createRoute(currentSession.id)
                            )
                        },
                        onDeleteClick = {
                            sessionViewModel.deleteSession(
                                currentSession.id,
                                onSuccess = { navController.popBackStack() },
                                onFailure = {
                                    errorMessage =
                                        it.message ?: "Could not delete the session."
                                }
                            )
                        },
                        onShiftClick = {
                            navController.navigate(
                                Screen.ShiftSchedule.createRoute(currentSession.id)
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.ShiftSchedule.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString("sessionId")
                ?: return@composable
            val sessionViewModel: SessionViewModel = viewModel()
            var currentSession by remember { mutableStateOf<Session?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(sessionId) {
                sessionViewModel.getSessionById(
                    sessionId,
                    onSuccess = {
                        currentSession = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the session."
                        isLoading = false
                    }
                )
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else if (currentSession == null) {
                Text(
                    text = errorMessage ?: "Session not found.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                currentSession?.let { session ->
                    ShiftScheduleScreen(
                        errorMessage = errorMessage,
                        isLoading = isLoading,
                        onShift15 = {
                            isLoading = true
                            shiftFollowingSessions(
                                sessionViewModel,
                                session,
                                15,
                                onSuccess = { navController.popBackStack() },
                                onFailure = {
                                    errorMessage = it.message
                                        ?: "Could not shift the schedule."
                                    isLoading = false
                                }
                            )
                        },
                        onShift30 = {
                            isLoading = true
                            shiftFollowingSessions(
                                sessionViewModel,
                                session,
                                30,
                                onSuccess = { navController.popBackStack() },
                                onFailure = {
                                    errorMessage = it.message
                                        ?: "Could not shift the schedule."
                                    isLoading = false
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
}

private fun shiftFollowingSessions(
    sessionViewModel: SessionViewModel,
    startingSession: Session,
    minutes: Long,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    sessionViewModel.getAllSessionsForEvent(
        startingSession.eventId,
        onSuccess = { sessions ->
            val updatedSessions = sessions
                .filter {
                    it.id == startingSession.id ||
                        TimeUtils.isAtOrAfter(it.startTime, startingSession.startTime)
                }
                .map {
                    it.copy(
                        startTime = TimeUtils.addMinutes(it.startTime, minutes),
                        endTime = TimeUtils.addMinutes(it.endTime, minutes)
                    )
                }

            sessionViewModel.updateSessions(
                updatedSessions,
                onSuccess,
                onFailure
            )
        },
        onFailure = onFailure
    )
}
