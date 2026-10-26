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

    object EventList :
        Screen("event_list")

    object EventDetails :
        Screen("event_details/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "event_details/$eventId"
    }

    object SessionList :
        Screen("session_list/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "session_list/$eventId"
    }

    object CreateSession :
        Screen("create_session/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "create_session/$eventId"
    }

    object SessionDetails :
        Screen("session_details/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "session_details/$sessionId"
    }

    object ShiftSchedule :
        Screen("shift_schedule/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "shift_schedule/$sessionId"
    }
}