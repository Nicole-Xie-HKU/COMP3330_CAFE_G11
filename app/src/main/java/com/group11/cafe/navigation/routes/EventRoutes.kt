package com.group11.cafe.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.model.UserRole
import com.group11.cafe.navigation.Screen
import com.group11.cafe.ui.event.AnnouncementsPage
import com.group11.cafe.ui.event.CreateEventScreen
import com.group11.cafe.ui.event.EventDetailsScreen
import com.group11.cafe.ui.event.EventListScreen
import com.group11.cafe.ui.event.ParticipantScheduleScreen
import com.group11.cafe.ui.help.AskHelpPage
import com.group11.cafe.ui.help.HelpListPage
import com.group11.cafe.utils.EventValidator
import com.group11.cafe.viewmodel.EventViewModel
import com.group11.cafe.viewmodel.SessionViewModel
import java.util.UUID

@Composable
fun ParticipantScheduleRoute(navController: NavHostController, eventId: String?) {
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

@Composable
fun CreateEventRoute(navController: NavHostController) {
    val eventViewModel: EventViewModel = viewModel()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    CreateEventScreen(
        errorMessage = errorMessage,
        onCreateEvent = { name, description, date ->
            val problem = EventValidator.check(name, description, date)
            if (problem != null) {
                errorMessage = problem
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
                organizerId = organizerId,
                createdAt = System.currentTimeMillis()
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

@Composable
fun EditEventRoute(navController: NavHostController, eventId: String?) {
    if (eventId == null) {
        return
    }
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
                val problem = EventValidator.check(name, description, date)
                if (problem != null) {
                    errorMessage = problem
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

@Composable
fun EventListRoute(navController: NavHostController) {
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

@Composable
fun EventDetailsRoute(
    navController: NavHostController,
    eventId: String?,
    currentUserRole: String
) {
    if (eventId == null) {
        return
    }
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

@Composable
fun AnnouncementsRoute(eventId: String?, currentUserRole: String) {
    AnnouncementsPage(
        eventId = eventId.orEmpty(),
        canPost = currentUserRole == UserRole.ORGANIZER
    )
}

@Composable
fun AskHelpRoute(eventId: String?) {
    AskHelpPage(
        eventId = eventId.orEmpty()
    )
}

@Composable
fun HelpListRoute(eventId: String?) {
    HelpListPage(
        eventId = eventId.orEmpty()
    )
}
