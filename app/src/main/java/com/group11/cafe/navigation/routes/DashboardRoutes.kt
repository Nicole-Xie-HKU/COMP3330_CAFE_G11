package com.group11.cafe.navigation.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.group11.cafe.navigation.Screen
import com.group11.cafe.ui.components.SettingsPage
import com.group11.cafe.ui.crew.CrewEventsPage
import com.group11.cafe.ui.crew.CrewTeamPage
import com.group11.cafe.ui.dashboard.CrewDashboard
import com.group11.cafe.ui.dashboard.DashboardRouter
import com.group11.cafe.ui.dashboard.OrganizerDashboard
import com.group11.cafe.ui.dashboard.ParticipantDashboard
import com.group11.cafe.ui.event.ParticipantEventsPage
import com.group11.cafe.ui.organizer.OrganizerMembersPage
import com.group11.cafe.ui.task.MyTasksPage

@Composable
fun DashboardRoute(navController: NavHostController) {
    DashboardRouter(navController)
}

@Composable
fun OrganizerDashboardRoute(navController: NavHostController) {
    OrganizerDashboard(
        onCreateEventClick = {
            navController.navigate(Screen.CreateEvent.route)
        },
        onViewEventsClick = {
            navController.navigate(Screen.EventList.route)
        }
    )
}

@Composable
fun CrewDashboardRoute(navController: NavHostController) {
    CrewDashboard(
        onTasksClick = {
            navController.navigate(Screen.MyTasks.route)
        },
        onEventsClick = {
            navController.navigate(Screen.CrewEvents.route)
        }
    )
}

@Composable
fun ParticipantDashboardRoute(navController: NavHostController) {
    ParticipantDashboard(
        onLastEventClick = {
            navController.navigate(
                Screen.ParticipantSchedule.createRoute(it)
            )
        }
    )
}

@Composable
fun ParticipantEventsTabRoute(navController: NavHostController) {
    ParticipantEventsPage(
        onEventClick = {
            navController.navigate(
                Screen.ParticipantSchedule.createRoute(it.id)
            )
        }
    )
}

@Composable
fun ParticipantSettingsRoute(navController: NavHostController) {
    SettingsPage(
        onLogoutClick = {
            logout(navController)
        }
    )
}

@Composable
fun MyTasksRoute() {
    MyTasksPage()
}

@Composable
fun CrewEventsRoute(navController: NavHostController) {
    CrewEventsPage(
        onEventClick = {
            navController.navigate(
                Screen.EventDetails.createRoute(it.id)
            )
        }
    )
}

@Composable
fun CrewTeamRoute() {
    CrewTeamPage()
}

@Composable
fun CrewSettingsRoute(navController: NavHostController) {
    SettingsPage(
        onLogoutClick = {
            logout(navController)
        }
    )
}

@Composable
fun OrganizerMembersRoute() {
    OrganizerMembersPage()
}

@Composable
fun OrganizerSettingsRoute(navController: NavHostController) {
    SettingsPage(
        onLogoutClick = {
            logout(navController)
        }
    )
}
