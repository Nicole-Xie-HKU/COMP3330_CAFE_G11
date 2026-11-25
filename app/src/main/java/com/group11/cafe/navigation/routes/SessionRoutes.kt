package com.group11.cafe.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.model.UserRole
import com.group11.cafe.navigation.Screen
import com.group11.cafe.ui.event.CreateSessionScreen
import com.group11.cafe.ui.event.ParticipantSessionPage
import com.group11.cafe.ui.event.SessionDetailsScreen
import com.group11.cafe.ui.event.SessionListScreen
import com.group11.cafe.ui.event.ShiftSchedulePage
import com.group11.cafe.ui.task.SessionTasksPage
import com.group11.cafe.utils.SessionValidator
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.SessionViewModel
import java.util.UUID

@Composable
fun ParticipantSessionRoute(sessionId: String?) {
    ParticipantSessionPage(
        sessionId = sessionId.orEmpty()
    )
}

@Composable
fun SessionListRoute(
    navController: NavHostController,
    eventId: String?,
    currentUserRole: String
) {
    if (eventId == null) {
        return
    }
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

@Composable
fun CreateSessionRoute(navController: NavHostController, eventId: String?) {
    if (eventId == null) {
        return
    }
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

@Composable
fun EditSessionRoute(navController: NavHostController, sessionId: String?) {
    if (sessionId == null) {
        return
    }
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

@Composable
fun SessionDetailsRoute(
    navController: NavHostController,
    sessionId: String?,
    currentUserRole: String
) {
    if (sessionId == null) {
        return
    }
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

@Composable
fun SessionTasksRoute(sessionId: String?, currentUserRole: String) {
    SessionTasksPage(
        sessionId = sessionId.orEmpty(),
        canEdit = currentUserRole == UserRole.ORGANIZER
    )
}

@Composable
fun ShiftScheduleRoute(sessionId: String?) {
    ShiftSchedulePage(
        sessionId = sessionId.orEmpty()
    )
}
