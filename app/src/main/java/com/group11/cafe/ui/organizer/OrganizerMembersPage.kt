package com.group11.cafe.ui.organizer

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
import com.group11.cafe.viewmodel.DashboardViewModel
import com.group11.cafe.viewmodel.OrganizationViewModel

@Composable
fun OrganizerMembersPage() {
    val dashboardViewModel: DashboardViewModel = viewModel()
    val organizationViewModel: OrganizationViewModel = viewModel()
    val organizerId = FirebaseAuth.getInstance().currentUser?.uid
    var organizer by remember { mutableStateOf<User?>(null) }
    var crewMembers by remember { mutableStateOf<List<User>>(emptyList()) }
    var memberships by remember { mutableStateOf<List<OrganizationMember>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(organizerId) {
        if (organizerId == null) {
            errorMessage = "Please sign in again."
            isLoading = false
            return@LaunchedEffect
        }

        dashboardViewModel.loadCurrentUser(
            onSuccess = { currentUser ->
                organizer = currentUser
                organizationViewModel.getMembershipsForOrganizer(
                    organizerId,
                    onSuccess = { memberships = it },
                    onFailure = { message = it.message ?: "Could not load teams." }
                )
                organizationViewModel.getCrewMembers(
                    organizerId,
                    onSuccess = {
                        crewMembers = it
                        isLoading = false
                    },
                    onFailure = {
                        errorMessage = it.message ?: "Could not load crew members."
                        isLoading = false
                    }
                )
            },
            onFailure = {
                errorMessage = it.message ?: "Could not load organizer."
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Members",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text("Crew invite code")
            Text(
                text = organizer?.inviteCode ?: "No invite code found",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Crew members")

            Spacer(modifier = Modifier.height(8.dp))

            if (message != null) {
                Text(message ?: "")
            }

            if (crewMembers.isEmpty()) {
                Text("No crew members have joined yet.")
            } else {
                LazyColumn {
                    items(crewMembers) { member ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(member.name)
                                Text(member.email)

                                TeamNameField(
                                    savedName = memberships
                                        .find { it.crewId == member.id }
                                        ?.teamName
                                        .orEmpty(),
                                    onSave = { teamName ->
                                        organizationViewModel.setTeamName(
                                            organizerId.orEmpty(),
                                            member.id,
                                            teamName,
                                            onSuccess = {
                                                message = "${member.name}: team saved."
                                            },
                                            onFailure = {
                                                message = it.message ?: "Could not save the team."
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamNameField(
    savedName: String,
    onSave: (String) -> Unit
) {
    var teamName by remember(savedName) { mutableStateOf(savedName) }

    OutlinedTextField(
        value = teamName,
        onValueChange = {
            teamName = it
        },
        label = {
            Text("Team (e.g. Registration)")
        },
        singleLine = true
    )

    Button(
        onClick = {
            onSave(teamName)
        }
    ) {
        Text("Save Team")
    }
}
