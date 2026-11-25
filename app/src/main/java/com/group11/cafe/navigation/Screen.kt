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

    object ParticipantEventsTab :
        Screen("participant_events_tab")

    object ParticipantSettings :
        Screen("participant_settings")

    object MyTasks :
        Screen("my_tasks")

    object CrewEvents :
        Screen("crew_events")

    object CrewTeam :
        Screen("crew_team")

    object CrewSettings :
        Screen("crew_settings")

    object OrganizerMembers :
        Screen("organizer_members")

    object OrganizerSettings :
        Screen("organizer_settings")

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

    object Announcements :
        Screen("announcements/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "announcements/$eventId"
    }

    object AskHelp :
        Screen("ask_help/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "ask_help/$eventId"
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

    object EditSession :
        Screen("edit_session/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "edit_session/$sessionId"
    }

    object ParticipantSchedule :
        Screen("participant_schedule/{eventId}") {

        fun createRoute(
            eventId: String
        ) = "participant_schedule/$eventId"
    }

    object ParticipantSession :
        Screen("participant_session/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "participant_session/$sessionId"
    }

    object SessionDetails :
        Screen("session_details/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "session_details/$sessionId"
    }

    object SessionTasks :
        Screen("session_tasks/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "session_tasks/$sessionId"
    }

    object ShiftSchedule :
        Screen("shift_schedule/{sessionId}") {

        fun createRoute(
            sessionId: String
        ) = "shift_schedule/$sessionId"
    }

    object AttendanceScanner :
        Screen("attendance_scanner/{eventId}/{sessionId}") {

        fun createRoute(
            eventId: String,
            sessionId: String
        ) = "attendance_scanner/$eventId/$sessionId"
    }

    object AttendanceList :
        Screen("attendance_list/{eventId}/{sessionId}") {

        fun createRoute(
            eventId: String,
            sessionId: String
        ) = "attendance_list/$eventId/$sessionId"
    }
}