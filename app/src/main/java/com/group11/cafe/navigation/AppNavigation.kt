package com.group11.cafe.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.navigation.routes.AnnouncementsRoute
import com.group11.cafe.navigation.routes.AskHelpRoute
import com.group11.cafe.navigation.routes.AttendanceListRoute
import com.group11.cafe.navigation.routes.AttendanceScannerRoute
import com.group11.cafe.navigation.routes.CreateEventRoute
import com.group11.cafe.navigation.routes.CreateSessionRoute
import com.group11.cafe.navigation.routes.CrewDashboardRoute
import com.group11.cafe.navigation.routes.CrewEventsRoute
import com.group11.cafe.navigation.routes.CrewSettingsRoute
import com.group11.cafe.navigation.routes.CrewTeamRoute
import com.group11.cafe.navigation.routes.DashboardRoute
import com.group11.cafe.navigation.routes.EditEventRoute
import com.group11.cafe.navigation.routes.EditSessionRoute
import com.group11.cafe.navigation.routes.EventDetailsRoute
import com.group11.cafe.navigation.routes.EventListRoute
import com.group11.cafe.navigation.routes.HelpListRoute
import com.group11.cafe.navigation.routes.LoginRoute
import com.group11.cafe.navigation.routes.MyTasksRoute
import com.group11.cafe.navigation.routes.OrganizerDashboardRoute
import com.group11.cafe.navigation.routes.OrganizerMembersRoute
import com.group11.cafe.navigation.routes.OrganizerSettingsRoute
import com.group11.cafe.navigation.routes.ParticipantDashboardRoute
import com.group11.cafe.navigation.routes.ParticipantEventsTabRoute
import com.group11.cafe.navigation.routes.ParticipantScheduleRoute
import com.group11.cafe.navigation.routes.ParticipantSessionRoute
import com.group11.cafe.navigation.routes.ParticipantSettingsRoute
import com.group11.cafe.navigation.routes.RegisterRoute
import com.group11.cafe.navigation.routes.SessionDetailsRoute
import com.group11.cafe.navigation.routes.SessionListRoute
import com.group11.cafe.navigation.routes.SessionTasksRoute
import com.group11.cafe.navigation.routes.ShiftScheduleRoute
import com.group11.cafe.ui.components.AppBackButton
import com.group11.cafe.ui.components.AppBottomNavigationBar
import com.group11.cafe.utils.GuestUtils
import com.group11.cafe.viewmodel.DashboardViewModel

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
                LoginRoute(navController)
            }

            composable(Screen.Register.route) {
                RegisterRoute(navController)
            }

            composable(Screen.Dashboard.route) {
                DashboardRoute(navController)
            }

            composable(Screen.OrganizerDashboard.route) {
                OrganizerDashboardRoute(navController)
            }

            composable(Screen.CrewDashboard.route) {
                CrewDashboardRoute(navController)
            }

            composable(
                route = Screen.AttendanceScanner.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType },
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                AttendanceScannerRoute(
                    backStackEntry.arguments?.getString("eventId"),
                    backStackEntry.arguments?.getString("sessionId")
                )
            }

            composable(
                route = Screen.AttendanceList.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType },
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                AttendanceListRoute(
                    backStackEntry.arguments?.getString("eventId"),
                    backStackEntry.arguments?.getString("sessionId")
                )
            }

            composable(Screen.ParticipantDashboard.route) {
                ParticipantDashboardRoute(navController)
            }

            composable(Screen.ParticipantEventsTab.route) {
                ParticipantEventsTabRoute(navController)
            }

            composable(Screen.ParticipantSettings.route) {
                ParticipantSettingsRoute(navController)
            }

            composable(Screen.MyTasks.route) {
                MyTasksRoute()
            }

            composable(Screen.CrewEvents.route) {
                CrewEventsRoute(navController)
            }

            composable(Screen.CrewTeam.route) {
                CrewTeamRoute()
            }

            composable(Screen.CrewSettings.route) {
                CrewSettingsRoute(navController)
            }

            composable(Screen.OrganizerMembers.route) {
                OrganizerMembersRoute()
            }

            composable(Screen.OrganizerSettings.route) {
                OrganizerSettingsRoute(navController)
            }

            composable(
                route = Screen.ParticipantSchedule.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ParticipantScheduleRoute(
                    navController,
                    backStackEntry.arguments?.getString("eventId")
                )
            }

            composable(
                route = Screen.ParticipantSession.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ParticipantSessionRoute(
                    backStackEntry.arguments?.getString("sessionId")
                )
            }

            composable(Screen.CreateEvent.route) {
                CreateEventRoute(navController)
            }

            composable(
                route = Screen.EditEvent.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                EditEventRoute(
                    navController,
                    backStackEntry.arguments?.getString("eventId")
                )
            }

            composable(Screen.EventList.route) {
                EventListRoute(navController)
            }

            composable(
                route = Screen.EventDetails.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                EventDetailsRoute(
                    navController,
                    backStackEntry.arguments?.getString("eventId"),
                    currentUserRole
                )
            }

            composable(
                route = Screen.Announcements.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                AnnouncementsRoute(
                    backStackEntry.arguments?.getString("eventId"),
                    currentUserRole
                )
            }

            composable(
                route = Screen.AskHelp.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                AskHelpRoute(
                    backStackEntry.arguments?.getString("eventId")
                )
            }

            composable(
                route = Screen.HelpList.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                HelpListRoute(
                    backStackEntry.arguments?.getString("eventId")
                )
            }

            composable(
                route = Screen.SessionList.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                SessionListRoute(
                    navController,
                    backStackEntry.arguments?.getString("eventId"),
                    currentUserRole
                )
            }

            composable(
                route = Screen.CreateSession.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                CreateSessionRoute(
                    navController,
                    backStackEntry.arguments?.getString("eventId")
                )
            }

            composable(
                route = Screen.EditSession.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                EditSessionRoute(
                    navController,
                    backStackEntry.arguments?.getString("sessionId")
                )
            }

            composable(
                route = Screen.SessionDetails.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                SessionDetailsRoute(
                    navController,
                    backStackEntry.arguments?.getString("sessionId"),
                    currentUserRole
                )
            }

            composable(
                route = Screen.SessionTasks.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                SessionTasksRoute(
                    backStackEntry.arguments?.getString("sessionId"),
                    currentUserRole
                )
            }

            composable(
                route = Screen.ShiftSchedule.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ShiftScheduleRoute(
                    backStackEntry.arguments?.getString("sessionId")
                )
            }
        }
    }
}
