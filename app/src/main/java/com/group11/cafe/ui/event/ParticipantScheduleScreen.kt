package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.Event
import com.group11.cafe.data.model.Session
import com.group11.cafe.data.model.Signup
import com.group11.cafe.ui.components.ShakeListener
import com.group11.cafe.ui.components.currentTime
import com.group11.cafe.utils.Feedback
import com.group11.cafe.utils.GuestUtils
import com.group11.cafe.utils.NetworkUtils
import com.group11.cafe.utils.Prefs
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.SignupViewModel

@Composable
fun ParticipantScheduleScreen(
    event: Event,
    sessions: List<Session>,
    isLoading: Boolean,
    errorMessage: String?,
    onAnnouncementsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSessionClick: (Session) -> Unit
) {
    val context = LocalContext.current
    val now = currentTime()
    val isOnline = remember(now) { NetworkUtils.isOnline(context) }
    val signupViewModel: SignupViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var signup by remember { mutableStateOf<Signup?>(null) }
    var participantName by remember { mutableStateOf("Participant") }
    var isSaving by remember { mutableStateOf(false) }
    var signupError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(event.id) {
        if (event.id.isNotBlank()) {
            Prefs.saveLastEvent(context, event.id, event.name)
        }
    }

    LaunchedEffect(Unit) {
        if (!GuestUtils.isGuest()) {
            dashboardViewModel.loadCurrentUser(
                onSuccess = { user ->
                    if (user.name.isNotBlank()) {
                        participantName = user.name
                    }
                },
                onFailure = {}
            )
        }
    }

    DisposableEffect(event.id, uid) {
        if (GuestUtils.isGuest() || uid.isNullOrBlank() || event.id.isBlank()) {
            signup = null
            onDispose { }
        } else {
            val listener = signupViewModel.listen(
                event.id,
                uid,
                onChange = { signup = it },
                onFailure = {
                    signupError = it.message ?: "Could not load your place."
                }
            )
            onDispose { listener.remove() }
        }
    }

    ShakeListener {
        Feedback.success(context)
        onHelpClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Event Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("${event.name} ${event.date}")

        if (!GuestUtils.isGuest()) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (uid.isNullOrBlank() || event.id.isBlank()) {
                        return@Button
                    }
                    isSaving = true
                    signupError = null
                    if (signup == null) {
                        signupViewModel.signUp(
                            Signup(
                                id = "${event.id}_$uid",
                                eventId = event.id,
                                eventName = event.name,
                                eventDate = event.date,
                                userId = uid,
                                participantName = participantName,
                                createdAt = System.currentTimeMillis()
                            ),
                            onSuccess = { isSaving = false },
                            onFailure = {
                                isSaving = false
                                signupError = it.message ?: "Could not save your place."
                            }
                        )
                    } else {
                        signupViewModel.cancel(
                            event.id,
                            uid,
                            onSuccess = { isSaving = false },
                            onFailure = {
                                isSaving = false
                                signupError = it.message ?: "Could not cancel your place."
                            }
                        )
                    }
                },
                enabled = !isSaving && event.id.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (signup == null) "I'm going" else "You're going")
            }

            if (signupError != null) {
                Text(
                    text = signupError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (!isOnline) {
            Text("Offline. Showing the saved schedule.")
        }

        Button(onClick = onAnnouncementsClick) {
            Text("Announcements")
        }

        Button(onClick = onHelpClick) {
            Text("Need Help (or shake)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        } else if (sessions.isEmpty()) {
            Text("No sessions have been published yet.")
        } else {
            SessionListScreen(
                sessions = sessions,
                eventDate = event.date,
                onSessionClick = onSessionClick
            )
        }
    }
}
