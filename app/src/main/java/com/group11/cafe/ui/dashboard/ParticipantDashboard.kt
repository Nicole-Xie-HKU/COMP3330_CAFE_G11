package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.group11.cafe.data.model.Signup
import com.group11.cafe.ui.components.DashboardHeader
import com.group11.cafe.ui.components.ParticipantQrCard
import com.group11.cafe.utils.GuestUtils
import com.group11.cafe.utils.Prefs
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.SignupViewModel

@Composable
fun ParticipantDashboard(
    onLastEventClick: (String) -> Unit
) {
    val context = LocalContext.current
    val lastEventId = Prefs.lastEventId(context)

    val dashboardViewModel: DashboardViewModel = viewModel()
    val signupViewModel: SignupViewModel = viewModel()
    val isGuest = GuestUtils.isGuest()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var qrCode by remember { mutableStateOf("") }
    var isLoadingQr by remember { mutableStateOf(!isGuest) }
    var signups by remember { mutableStateOf<List<Signup>>(emptyList()) }
    var signupsLoaded by remember { mutableStateOf(false) }
    var signupError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isGuest) {
        if (isGuest) {
            return@LaunchedEffect
        }

        dashboardViewModel.loadCurrentUser(
            onSuccess = {
                qrCode = it.participantQrCode
                isLoadingQr = false
            },
            onFailure = {
                isLoadingQr = false
            }
        )
    }

    DisposableEffect(uid, isGuest) {
        if (isGuest || uid.isNullOrBlank()) {
            onDispose { }
        } else {
            val listener = signupViewModel.listenForUser(
                uid,
                onChange = {
                    signups = it.sortedBy { signup -> signup.eventName }
                    signupsLoaded = true
                },
                onFailure = {
                    signupError = it.message ?: "Could not load your events."
                    signupsLoaded = true
                }
            )
            onDispose { listener.remove() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        DashboardHeader(
            fallbackRole = "Participant"
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isGuest) {
            Text("Create a participant account to get a check-in pass.")
            Spacer(modifier = Modifier.height(16.dp))
        } else if (!isLoadingQr) {
            ParticipantQrCard(qrCode = qrCode)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isGuest) {
            Text(
                text = "My events",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!signupsLoaded) {
                CircularProgressIndicator()
            } else if (signupError != null) {
                Text(
                    text = signupError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (signups.isEmpty()) {
                Text("You have not saved an event yet.")
            } else {
                signups.forEach { signup ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLastEventClick(signup.eventId) }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(signup.eventName)
                        Text(signup.eventDate)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (lastEventId.isNotBlank()) {
            Button(onClick = { onLastEventClick(lastEventId) }) {
                Text("Open ${Prefs.lastEventName(context)}")
            }
        }
    }
}
