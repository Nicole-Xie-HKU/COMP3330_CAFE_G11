package com.group11.cafe.ui.attendance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group11.cafe.data.model.AttendanceRecord
import com.group11.cafe.data.model.Signup
import com.group11.cafe.viewmodel.AttendanceViewModel
import com.group11.cafe.viewmodel.SignupViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceListPage(
    eventId: String,
    sessionId: String
) {
    val attendanceViewModel: AttendanceViewModel = viewModel()
    val signupViewModel: SignupViewModel = viewModel()
    var records by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var signups by remember { mutableStateOf<List<Signup>>(emptyList()) }
    var attendanceReady by remember { mutableStateOf(false) }
    var signupsReady by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(eventId, sessionId) {
        val attendanceListener = attendanceViewModel.listenForSession(
            eventId,
            sessionId,
            onChange = {
                records = it
                attendanceReady = true
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load attendance."
                attendanceReady = true
            }
        )
        val signupListener = signupViewModel.listenForEvent(
            eventId,
            onChange = {
                signups = it
                signupsReady = true
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load signups."
                signupsReady = true
            }
        )

        onDispose {
            attendanceListener.remove()
            signupListener.remove()
        }
    }

    val checkedIn = records.sortedBy { it.participantName }
    val checkedInIds = records.map { it.participantId }.toSet()
    val notCheckedIn = signups.filter { it.userId !in checkedInIds }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Attendance",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!attendanceReady || !signupsReady) {
            CircularProgressIndicator()
        } else {
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("${records.size} of ${signups.size} checked in")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Not checked in yet",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (notCheckedIn.isEmpty()) {
                Text("Everyone who saved a place has checked in.")
            } else {
                notCheckedIn.forEach { signup ->
                    Text(
                        text = signup.participantName,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Checked in",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            checkedIn.forEach { record ->
                Text(
                    text = attendanceLine(record),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

private fun attendanceLine(record: AttendanceRecord): String {
    val stamp = record.checkedInAt ?: return record.participantName
    val time = stamp.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"))
    return "${record.participantName}, $time"
}
