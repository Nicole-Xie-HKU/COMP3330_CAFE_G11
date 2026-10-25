package com.group11.cafe.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Register : Screen("register")

    object Dashboard : Screen("dashboard")

    object OrganizerDashboard :
        Screen("organizer_dashboard")

    object CrewDashboard :
        Screen("crew_dashboard")

    object ParticipantDashboard :
        Screen("participant_dashboard")

    object CreateEvent :
        Screen("create_event")

}