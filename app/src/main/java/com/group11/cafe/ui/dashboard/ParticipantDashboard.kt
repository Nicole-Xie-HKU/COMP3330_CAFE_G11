package com.group11.cafe.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.group11.cafe.ui.components.DashboardHeader
import com.group11.cafe.ui.components.ParticipantQrCard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.utils.Prefs
import com.group11.cafe.viewmodel.DashboardViewModel
import androidx.compose.runtime.*
import com.group11.cafe.utils.ParticipantQrParser
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ParticipantDashboard(
    onLastEventClick: (String) -> Unit
) {
    val context = LocalContext.current
    val lastEventId = Prefs.lastEventId(context)

    val dashboardViewModel: DashboardViewModel = viewModel()
    var qrCode by remember { mutableStateOf("") }
    var isLoadingQr by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            qrCode = ParticipantQrParser.GUEST_QR_CODE
            isLoadingQr = false
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

        if (!isLoadingQr) {
            ParticipantQrCard(qrCode = qrCode)
            Spacer(modifier = Modifier.height(16.dp))
        }

        val isSignedIn = FirebaseAuth.getInstance().currentUser != null

        if (isSignedIn && lastEventId.isNotBlank()) {
            Button(onClick = { onLastEventClick(lastEventId) }) {
                Text("Open ${Prefs.lastEventName(context)}")
            }
        }
    }
}
