package com.group11.cafe.ui.crew

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.data.model.OrganizationMember
import com.group11.cafe.data.model.User
import com.group11.cafe.viewmodel.OrganizationViewModel

@Composable
fun CrewTeamPage() {
    val organizationViewModel: OrganizationViewModel = viewModel()
    val crewId = FirebaseAuth.getInstance().currentUser?.uid
    var inviteCode by remember { mutableStateOf("") }
    var organizers by remember { mutableStateOf<List<User>>(emptyList()) }
    var memberships by remember { mutableStateOf<List<OrganizationMember>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isJoining by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    fun loadTeams() {
        if (crewId == null) {
            message = "Please sign in again."
            isLoading = false
            return
        }

        organizationViewModel.getMembershipsForCrew(
            crewId,
            onSuccess = { memberships = it },
            onFailure = { message = it.message ?: "Could not load teams." }
        )
        organizationViewModel.getOrganizersForCrew(
            crewId,
            onSuccess = {
                organizers = it
                isLoading = false
            },
            onFailure = {
                message = it.message ?: "Could not load teams."
                isLoading = false
            }
        )
    }

    LaunchedEffect(crewId) {
        loadTeams()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Team",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inviteCode,
            onValueChange = {
                inviteCode = it.uppercase()
            },
            label = {
                Text("Organizer invite code")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            enabled = !isJoining,
            onClick = {
                if (crewId == null) {
                    message = "Please sign in again."
                    return@Button
                }

                if (inviteCode.isBlank()) {
                    message = "Enter an invite code."
                    return@Button
                }

                isJoining = true
                message = null
                organizationViewModel.joinOrganizer(
                    inviteCode,
                    crewId,
                    onSuccess = { organizer ->
                        inviteCode = ""
                        message = "Joined ${organizer.name}."
                        isJoining = false
                        loadTeams()
                    },
                    onFailure = {
                        message = it.message ?: "Could not join the organizer."
                        isJoining = false
                    }
                )
            }
        ) {
            Text(if (isJoining) "Joining..." else "Join Organizer")
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(message ?: "")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Your teams")

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (organizers.isEmpty()) {
            Text("You have not joined an organizer yet.")
        } else {
            LazyColumn {
                items(organizers) { organizer ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(organizer.name)
                            Text(organizer.email)

                            val teamName = memberships
                                .find { it.organizerId == organizer.id }
                                ?.teamName
                                .orEmpty()

                            if (teamName.isNotBlank()) {
                                Text("Your team: $teamName")
                            }
                        }
                    }
                }
            }
        }
    }
}
