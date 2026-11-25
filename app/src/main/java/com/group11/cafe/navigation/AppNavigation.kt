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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.group11.cafe.data.model.UserRole
import com.group11.cafe.ui.attendance.AttendanceListPage
import com.group11.cafe.ui.attendance.AttendanceScannerPage
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.auth.RegisterScreen
import com.group11.cafe.ui.crew.CrewEventsPage
import com.group11.cafe.ui.dashboard.CrewDashboard
import com.group11.cafe.ui.dashboard.DashboardRouter
import com.group11.cafe.ui.dashboard.OrganizerDashboard
import com.group11.cafe.ui.dashboard.ParticipantDashboard
import com.group11.cafe.ui.event.AnnouncementsPage
import com.group11.cafe.ui.event.CreateEventScreen
import com.group11.cafe.ui.event.CreateSessionScreen
import com.group11.cafe.ui.event.EventDetailsScreen
import com.group11.cafe.ui.event.EventListScreen
import com.group11.cafe.ui.event.ParticipantScheduleScreen
import com.group11.cafe.ui.event.ParticipantEventsPage
import com.group11.cafe.ui.event.ParticipantSessionPage
import com.group11.cafe.ui.event.SessionDetailsScreen
import com.group11.cafe.ui.event.SessionListScreen
import com.group11.cafe.ui.event.ShiftSchedulePage
import com.group11.cafe.ui.crew.CrewTeamPage
import com.group11.cafe.ui.components.AppBackButton
import com.group11.cafe.ui.components.AppBottomNavigationBar
import com.group11.cafe.ui.components.SettingsPage
import com.group11.cafe.ui.help.AskHelpPage
import com.group11.cafe.ui.help.HelpListPage
import com.group11.cafe.ui.organizer.OrganizerMembersPage
import com.group11.cafe.ui.task.TaskManagementScreen
import com.group11.cafe.ui.task.MyTasksPage
import com.group11.cafe.ui.task.SessionTasksPage
import com.group11.cafe.utils.GuestUtils
import com.group11.cafe.utils.SessionValidator
import com.group11.cafe.utils.TimeUtils
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.LoginViewModel
import com.group11.cafe.viewmodel.RegisterViewModel
import com.group11.cafe.viewmodel.SessionViewModel
import java.util.UUID

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppNavigation() {
    val navController = rememberNavController()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val signedInUserId = FirebaseAuth.getInstance().currentUser?.uid
    var currentUserRole by remember { mutableStateOf("") }
    val startRoute = remember {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Screen.Login.route
        } else {
            Screen.Dashboard.route
        }
    }
    val bottomNavigationItems = if (!GuestUtils.isGuest()) {
        bottomNavigationItemsForRole(currentUserRole)
    } else if (currentRoute in listOf(
            Screen.ParticipantDashboard.route,
            Screen.ParticipantEventsTab.route,
            Screen.ParticipantSettings.route
        )) {
        participantNavigationItems
    } else {
        null
    }
    val rootRoutes = setOf(
        Screen.OrganizerDashboard.route,
        Screen.CrewDashboard.route,
        Screen.ParticipantDashboard.route,
        Screen.ParticipantEventsTab.route,
        Screen.ParticipantSettings.route,
        Screen.CrewEvents.route,
        Screen.CrewTeam.route,
        Screen.CrewSettings.route,
        Screen.OrganizerMembers.route,
        Screen.OrganizerSettings.route
    )

    LaunchedEffect(signedInUserId) {
        if (signedInUserId == null) {
            currentUserRole = ""
            return@LaunchedEffect
        }

        dashboardViewModel.loadRole {
            currentUserRole = it.trim()
        }
    }

    Scaffold(
        topBar = {
            if (
                currentRoute != null &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Dashboard.route &&
                currentRoute !in rootRoutes
            ) {
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
        },
        bottomBar = {
            if (bottomNavigationItems != null) {
                AppBottomNavigationBar(
                    items = bottomNavigationItems,
                    currentRoute = currentRoute,
                    onItemClick = { destinationRoute ->
                        if (destinationRoute != currentRoute) {
                            navController.navigate(destinationRoute) {
                                popUpTo(bottomNavigationItems.first().route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
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
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Enter your email and password."
                        return@LoginScreen
                    }

                    isLoading = true
                    errorMessage = null

                    loginViewModel.login(
                        email.trim(),
                        password,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onFailure = {
                            isLoading = false
                            errorMessage = it.message ?: "Login failed."
                        }
                    )
                },
                onGuestClick = {
                    isLoading = true
                    errorMessage = null

                    loginViewModel.loginAsGuest(
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.ParticipantDashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onFailure = {
                            isLoading = false
                            errorMessage = it.message ?: "Guest login failed."
                        }
                    )
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
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Enter your name, email and password."
                        return@RegisterScreen
                    }

                    isLoading = true
                    errorMessage = null

                    registerViewModel.register(
                        name.trim(),
                        email.trim(),
                        password,
                        role,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
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
                },
                onManageTasksClick = {
                    navController.navigate(Screen.OrganizerTasks.route)
                }
            )
        }

        composable(Screen.CrewDashboard.route) {
            CrewDashboard(
                onTasksClick = {
                    navController.navigate(Screen.MyTasks.route)
                },
                onEventsClick = {
                    navController.navigate(Screen.CrewEvents.route)
                },
                onAssignedTasksClick = {
                    navController.navigate(Screen.CrewTasks.route)
                }
            )
        }

        composable(Screen.OrganizerTasks.route) {
            TaskManagementScreen(isOrganizer = true)
        }

        composable(Screen.CrewTasks.route) {
            TaskManagementScreen(isOrganizer = false)
        }

        composable(
            route = Screen.AttendanceScanner.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AttendanceScannerPage(
                eventId = backStackEntry.arguments?.getString("eventId").orEmpty(),
                sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            )
        }

        composable(
            route = Screen.AttendanceList.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AttendanceListPage(
                eventId = backStackEntry.arguments?.getString("eventId").orEmpty(),
                sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            )
        }

        composable(Screen.ParticipantDashboard.route) {
            ParticipantDashboard(
                onLastEventClick = {
                    navController.navigate(
                        Screen.ParticipantSchedule.createRoute(it)
                    )
                }
            )
        }

        composable(Screen.ParticipantEventsTab.route) {
            ParticipantEventsPage(
                onEventClick = {
                    navController.navigate(
                        Screen.ParticipantSchedule.createRoute(it.id)
                    )
                }
            )
        }

        composable(Screen.ParticipantSettings.route) {
            SettingsPage(
                onLogoutClick = {
                    logout(navController)
                }
            )
        }

        composable(Screen.MyTasks.route) {
            MyTasksPage()
        }

        composable(Screen.CrewEvents.route) {
            CrewEventsPage(
                onEventClick = {
                    navController.navigate(
                        Screen.EventDetails.createRoute(it.id)
                    )
                }
            )
        }

        composable(Screen.CrewTeam.route) {
            CrewTeamPage()
        }

        composable(Screen.CrewSettings.route) {
            SettingsPage(
                onLogoutClick = {
                    logout(navController)
                }
            )
        }

        composable(Screen.OrganizerMembers.route) {
            OrganizerMembersPage()
        }

        composable(Screen.OrganizerSettings.route) {
            SettingsPage(
                onLogoutClick = {
                    logout(navController)
                }
            )
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
            val eventViewModel: EventViewModel = viewModel()
            var event by remember { mutableStateOf(Event()) }
            var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(eventId) {
                eventViewModel.getEventById(
                    eventId,
                    onSuccess = { event = it },
                    onFailure = { errorMessage = it.message }
                )
            }

            DisposableEffect(eventId) {
                val listener = sessionViewModel.listenToSessions(
                    eventId,
                    onChange = {
                        sessions = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the schedule."
                        isLoading = false
                    }
                )

                onDispose {
                    listener.remove()
                }
            }

            ParticipantScheduleScreen(
                event = event,
                sessions = sessions,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onAnnouncementsClick = {
                    navController.navigate(
                        Screen.Announcements.createRoute(eventId)
                    )
                },
                onHelpClick = {
                    navController.navigate(
                        Screen.AskHelp.createRoute(eventId)
                    )
                },
                onSessionClick = {
                    navController.navigate(
                        Screen.ParticipantSession.createRoute(it.id)
                    )
                }
            )
        }

        composable(
            route = Screen.ParticipantSession.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ParticipantSessionPage(
                sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            )
        }

        composable(Screen.CreateEvent.route) {
            val eventViewModel: EventViewModel = viewModel()

            var errorMessage by remember { mutableStateOf<String?>(null) }

            CreateEventScreen(
                errorMessage = errorMessage,
                onCreateEvent = { name, description, date ->
                    if (name.isBlank()) {
                        errorMessage = "Enter an event name."
                        return@CreateEventScreen
                    }
                    if (!TimeUtils.isDate(date)) {
                        errorMessage = "Use a date like 2026-11-30."
                        return@CreateEventScreen
                    }

                    val organizerId = FirebaseAuth
                        .getInstance()
                        .currentUser
                        ?.uid
                        ?: return@CreateEventScreen

                    val event = Event(
                        id = UUID.randomUUID().toString(),
                        name = name.trim(),
                        description = description.trim(),
                        date = date.trim(),
                        organizerId = organizerId
                    )

                    eventViewModel.createEvent(
                        event,
                        onSuccess = { navController.popBackStack() },
                        onFailure = {
                            errorMessage = it.message ?: "Could not create the event."
                        }
                    )
                }
            )
        }

        composable(
            route = Screen.EditEvent.route,
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
            var loaded by remember { mutableStateOf(false) }

            LaunchedEffect(eventId) {
                eventViewModel.getEventById(
                    eventId,
                    onSuccess = {
                        event = it
                        loaded = true
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Event not found."
                        loaded = true
                    }
                )
            }

            val currentEvent = event
            val signedInUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (!loaded) {
                CircularProgressIndicator()
            } else if (currentEvent == null) {
                Text(
                    text = errorMessage ?: "Event not found.",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (currentEvent.organizerId != signedInUserId) {
                Text(
                    text = "You cannot edit this event.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                CreateEventScreen(
                    screenTitle = "Edit Event",
                    buttonText = "Save Changes",
                    initialName = currentEvent.name,
                    initialDescription = currentEvent.description,
                    initialDate = currentEvent.date,
                    errorMessage = errorMessage,
                    onCreateEvent = { name, description, date ->
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid == null || uid != currentEvent.organizerId) {
                            errorMessage = "You cannot edit this event."
                            return@CreateEventScreen
                        }
                        if (name.isBlank()) {
                            errorMessage = "Enter an event name."
                            return@CreateEventScreen
                        }
                        if (!TimeUtils.isDate(date)) {
                            errorMessage = "Use a date like 2026-11-30."
                            return@CreateEventScreen
                        }

                        eventViewModel.updateEvent(
                            currentEvent.copy(
                                name = name.trim(),
                                description = description.trim(),
                                date = date.trim()
                            ),
                            onSuccess = { navController.popBackStack() },
                            onFailure = {
                                errorMessage = it.message ?: "Could not save the event."
                            }
                        )
                    }
                )
            }
        }

        composable(Screen.EventList.route) {
            val eventViewModel: EventViewModel = viewModel()
            var events by remember { mutableStateOf<List<Event>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            val organizerId = FirebaseAuth.getInstance().currentUser?.uid
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner, organizerId) {
                fun loadEvents() {
                    if (organizerId == null) {
                        errorMessage = "Please sign in again."
                        isLoading = false
                        return
                    }

                    eventViewModel.getEventsForOrganizer(
                        organizerId,
                        onSuccess = {
                            events = it
                            isLoading = false
                            errorMessage = null
                        },
                        onFailure = {
                            errorMessage = it.message ?: "Could not load events."
                            isLoading = false
                        }
                    )
                }

                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        loadEvents()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                loadEvents()
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
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
            var received by remember { mutableStateOf(false) }

            DisposableEffect(eventId) {
                val listener = eventViewModel.listenToEvent(
                    eventId,
                    onChange = { updated ->
                        event = updated
                        received = true
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the event."
                    }
                )

                onDispose {
                    listener.remove()
                }
            }

            if (!received && errorMessage == null) {
                CircularProgressIndicator()
            } else {
                val currentEvent = event

                if (currentEvent == null) {
                    Text(
                        text = errorMessage ?: "Event not found.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    EventDetailsScreen(
                        event = currentEvent,
                        canEdit = currentUserRole == UserRole.ORGANIZER &&
                            currentEvent.organizerId ==
                            FirebaseAuth.getInstance().currentUser?.uid,
                        errorMessage = errorMessage,
                        onAnnouncementsClick = {
                            navController.navigate(
                                Screen.Announcements.createRoute(currentEvent.id)
                            )
                        },
                        onHelpClick = {
                            navController.navigate(
                                Screen.HelpList.createRoute(currentEvent.id)
                            )
                        },
                        onViewSessionsClick = {
                            navController.navigate(
                                Screen.SessionList.createRoute(currentEvent.id)
                            )
                        },
                        onEditClick = {
                            navController.navigate(
                                Screen.EditEvent.createRoute(currentEvent.id)
                            )
                        },
                        onDeleteClick = {
                            errorMessage = null
                            eventViewModel.deleteEvent(
                                currentEvent.id,
                                onSuccess = { navController.popBackStack() },
                                onFailure = {
                                    errorMessage =
                                        it.message ?: "Could not delete the event."
                                }
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.Announcements.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AnnouncementsPage(
                eventId = backStackEntry.arguments?.getString("eventId").orEmpty(),
                canPost = currentUserRole == UserRole.ORGANIZER
            )
        }

        composable(
            route = Screen.AskHelp.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AskHelpPage(
                eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            )
        }

        composable(
            route = Screen.HelpList.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            HelpListPage(
                eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            )
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
            val eventViewModel: EventViewModel = viewModel()
            var event by remember { mutableStateOf(Event()) }
            var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(eventId) {
                eventViewModel.getEventById(
                    eventId,
                    onSuccess = { event = it },
                    onFailure = { errorMessage = it.message }
                )
            }

            DisposableEffect(eventId) {
                val listener = sessionViewModel.listenToSessions(
                    eventId,
                    onChange = {
                        sessions = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load sessions."
                        isLoading = false
                    }
                )

                onDispose {
                    listener.remove()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (currentUserRole == UserRole.ORGANIZER) {
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
                }

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                } else if (sessions.isEmpty()) {
                    Text("No sessions have been created yet.")
                } else {
                    SessionListScreen(
                        sessions = sessions,
                        eventDate = event.date,
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
            var existingSessions by remember { mutableStateOf<List<Session>>(emptyList()) }

            DisposableEffect(eventId) {
                val listener = sessionViewModel.listenToSessions(
                    eventId,
                    onChange = { existingSessions = it },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load sessions."
                    }
                )

                onDispose {
                    listener.remove()
                }
            }

            CreateSessionScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onSubmit = { title, location, startTime, endTime, fixedTime ->
                    val problem = SessionValidator.check(title, location, startTime, endTime)
                    if (problem != null) {
                        errorMessage = problem
                        return@CreateSessionScreen
                    }

                    val clash = SessionValidator.overlap(
                        existingSessions,
                        startTime,
                        endTime,
                        null
                    )
                    if (clash != null) {
                        errorMessage = clash
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
                        endTime = endTime.trim(),
                        fixedTime = fixedTime
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
                    var existingSessions by remember {
                        mutableStateOf<List<Session>>(emptyList())
                    }

                    DisposableEffect(currentSession.eventId) {
                        val listener = sessionViewModel.listenToSessions(
                            currentSession.eventId,
                            onChange = { existingSessions = it },
                            onFailure = {
                                errorMessage = it.message ?: "Could not load sessions."
                            }
                        )

                        onDispose {
                            listener.remove()
                        }
                    }

                    CreateSessionScreen(
                        screenTitle = "Edit Session",
                        buttonText = "Save Changes",
                        initialTitle = currentSession.title,
                        initialLocation = currentSession.location,
                        initialStartTime = currentSession.startTime,
                        initialEndTime = currentSession.endTime,
                        initialFixedTime = currentSession.fixedTime,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onSubmit = { title, location, startTime, endTime, fixedTime ->
                            val sameStart = startTime.trim() == currentSession.startTime
                            val problem = SessionValidator.check(title, location, startTime, endTime)
                            if (problem != null) {
                                errorMessage = problem
                                return@CreateSessionScreen
                            }

                            val clash = SessionValidator.overlap(
                                existingSessions,
                                startTime,
                                endTime,
                                currentSession.id
                            )
                            if (clash != null) {
                                errorMessage = clash
                                return@CreateSessionScreen
                            }

                            isLoading = true
                            errorMessage = null
                            sessionViewModel.updateSession(
                                currentSession.copy(
                                    title = title.trim(),
                                    location = location.trim(),
                                    startTime = startTime.trim(),
                                    endTime = endTime.trim(),
                                    fixedTime = fixedTime,
                                    movedMinutes = if (sameStart) currentSession.movedMinutes else 0
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
            var received by remember { mutableStateOf(false) }

            DisposableEffect(sessionId) {
                val listener = sessionViewModel.listenToSession(
                    sessionId,
                    onChange = { updated ->
                        session = updated
                        received = true
                        errorMessage = null
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load the session."
                    }
                )

                onDispose {
                    listener.remove()
                }
            }

            if (!received && errorMessage == null) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else if (session == null) {
                Text(
                    text = "Session not found.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                session?.let { currentSession ->
                    SessionDetailsScreen(
                        session = currentSession,
                        canEdit = currentUserRole == UserRole.ORGANIZER,
                        onScanClick = {
                            navController.navigate(
                                Screen.AttendanceScanner.createRoute(
                                    currentSession.eventId,
                                    currentSession.id
                                )
                            )
                        },
                        onAttendanceClick = {
                            navController.navigate(
                                Screen.AttendanceList.createRoute(
                                    currentSession.eventId,
                                    currentSession.id
                                )
                            )
                        },
                        onTasksClick = {
                            navController.navigate(
                                Screen.SessionTasks.createRoute(currentSession.id)
                            )
                        },
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
            route = Screen.SessionTasks.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SessionTasksPage(
                sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty(),
                canEdit = currentUserRole == UserRole.ORGANIZER
            )
        }

        composable(
            route = Screen.ShiftSchedule.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ShiftSchedulePage(
                sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            )
        }
    }
}
}

private fun logout(
    navController: androidx.navigation.NavController
) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate(Screen.Login.route) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }
}
