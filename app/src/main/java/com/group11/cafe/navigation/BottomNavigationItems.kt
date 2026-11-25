package com.group11.cafe.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val participantNavigationItems = listOf(
    BottomNavigationItem(
        label = "Home",
        route = Screen.ParticipantDashboard.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        label = "Events",
        route = Screen.ParticipantEventsTab.route,
        icon = Icons.Outlined.Event
    ),
    BottomNavigationItem(
        label = "Settings",
        route = Screen.ParticipantSettings.route,
        icon = Icons.Outlined.Settings
    )
)

val crewNavigationItems = listOf(
    BottomNavigationItem(
        label = "Home",
        route = Screen.CrewDashboard.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        label = "Events",
        route = Screen.CrewEvents.route,
        icon = Icons.Outlined.Event
    ),
    BottomNavigationItem(
        label = "Team",
        route = Screen.CrewTeam.route,
        icon = Icons.Outlined.Groups
    ),
    BottomNavigationItem(
        label = "Settings",
        route = Screen.CrewSettings.route,
        icon = Icons.Outlined.Settings
    )
)

val organizerNavigationItems = listOf(
    BottomNavigationItem(
        label = "Home",
        route = Screen.OrganizerDashboard.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        label = "Members",
        route = Screen.OrganizerMembers.route,
        icon = Icons.Outlined.Groups
    ),
    BottomNavigationItem(
        label = "Settings",
        route = Screen.OrganizerSettings.route,
        icon = Icons.Outlined.Settings
    )
)

fun bottomNavigationItemsFor(route: String?): List<BottomNavigationItem>? {
    return when (route) {
        in participantNavigationItems.map { it.route } -> participantNavigationItems
        in crewNavigationItems.map { it.route } -> crewNavigationItems
        in organizerNavigationItems.map { it.route } -> organizerNavigationItems
        else -> null
    }
}

fun bottomNavigationItemsForRole(role: String): List<BottomNavigationItem>? {
    return when (role) {
        "Organizer" -> organizerNavigationItems
        "Crew" -> crewNavigationItems
        "Participant" -> participantNavigationItems
        else -> null
    }
}
