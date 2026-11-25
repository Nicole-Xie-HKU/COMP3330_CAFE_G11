package com.group11.cafe.ui.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.User
import com.group11.cafe.utils.Feedback
import com.group11.cafe.utils.ParticipantQrParser
import com.group11.cafe.viewmodel.AttendanceViewModel

@Composable
fun AttendanceScannerPage(
    eventId: String,
    sessionId: String
) {
    val attendanceViewModel: AttendanceViewModel = viewModel()
    var message by remember { mutableStateOf("Scan a participant's QR code.") }
    var scanNumber by remember { mutableIntStateOf(0) }
    var isDone by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun finish(text: String, success: Boolean) {
        message = text
        isDone = true
        isSuccess = success

        if (success) {
            Feedback.success(context)
        } else {
            Feedback.error(context)
        }
    }

    fun checkIn(user: User) {
        attendanceViewModel.checkIn(
            eventId = eventId,
            sessionId = sessionId,
            participantId = user.id,
            participantName = user.name,
            checkedInBy = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
            onSuccess = { isNewCheckIn ->
                if (isNewCheckIn) {
                    finish("${user.name} checked in.", true)
                } else {
                    finish("${user.name} is already checked in.", false)
                }
            },
            onFailure = {
                finish(it.message ?: "Could not check in.", false)
            }
        )
    }

    fun onScan(qrValue: String) {
        val identity = ParticipantQrParser.parse(qrValue)

        if (identity == null) {
            finish("This is not a CAFE QR code.", false)
        } else if (identity.isGuest) {
            finish("Guests must register before checking in.", false)
        } else {
            message = "Checking in..."
            attendanceViewModel.getParticipant(
                identity.id,
                onSuccess = { checkIn(it) },
                onFailure = { finish(it.message ?: "Participant not found.", false) }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // restart the camera
            key(scanNumber) {
                AttendanceScanner(
                    statusMessage = message,
                    onQrScanned = { onScan(it) }
                )
            }
        }

        AnimatedVisibility(
            visible = isSuccess,
            enter = scaleIn() + fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "\u2713",
                fontSize = 64.sp,
                color = Color(0xFF2E7D32)
            )
        }

        if (isDone) {
            Button(
                onClick = {
                    isDone = false
                    isSuccess = false
                    message = "Scan a participant's QR code."
                    scanNumber += 1
                }
            ) {
                Text("Scan Next")
            }
        }
    }
}
