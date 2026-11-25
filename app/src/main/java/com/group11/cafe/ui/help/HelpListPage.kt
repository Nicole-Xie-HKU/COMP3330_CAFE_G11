package com.group11.cafe.ui.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.group11.cafe.data.model.HelpRequest
import com.group11.cafe.utils.Feedback
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.HelpViewModel

@Composable
fun HelpListPage(
    eventId: String
) {
    val helpViewModel: HelpViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val context = LocalContext.current
    var requests by remember { mutableStateOf<List<HelpRequest>>(emptyList()) }
    var hasLoaded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Crew") }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        dashboardViewModel.loadCurrentUser(
            onSuccess = { name = it.name },
            onFailure = { message = it.message }
        )
    }

    DisposableEffect(eventId) {
        val listener = helpViewModel.listen(
            eventId,
            onChange = { all ->
                val waiting = all.filter { it.status != "done" }

                val hasNew = waiting.any { new ->
                    requests.none { it.id == new.id }
                }

                if (hasLoaded && hasNew) {
                    Feedback.success(context)
                }

                hasLoaded = true
                requests = waiting.sortedBy { it.createdAt }
            },
            onFailure = { message = it.message }
        )

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Help Requests",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (message != null) {
            Text(message ?: "")
        }

        if (requests.isEmpty()) {
            Text("Nobody needs help right now.")
        }

        LazyColumn {
            items(requests) { request ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${request.requesterName} at ${request.place}")
                        Text(request.note)

                        if (request.status == "open") {
                            Button(
                                onClick = {
                                    helpViewModel.claim(
                                        request.id,
                                        name,
                                        onSuccess = { claimed ->
                                            message = if (claimed) {
                                                "You claimed this request."
                                            } else {
                                                "Someone else already claimed it."
                                            }
                                        },
                                        onFailure = { message = it.message }
                                    )
                                }
                            ) {
                                Text("I Will Go")
                            }
                        } else {
                            Text("${request.crewName} is going")

                            Button(
                                onClick = {
                                    helpViewModel.finish(
                                        request.id,
                                        onSuccess = { message = "Marked as done." },
                                        onFailure = { message = it.message }
                                    )
                                }
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }
    }
}
