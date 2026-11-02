package com.group11.cafe.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import android.util.Log

@Composable
fun RegisterScreen(
    onRegisterClick: (
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var role by remember {
        mutableStateOf("Participant")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Role")

        Row {

            listOf(
                "Organizer",
                "Crew",
                "Participant"
            ).forEach { currentRole ->

                FilterChip(
                    selected = role == currentRole,
                    onClick = {
                        role = currentRole
                    },
                    label = {
                        Text(currentRole)
                    }
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                Log.d("REGISTER", "Button clicked")

                onRegisterClick(
                    name,
                    email,
                    password,
                    role
                )
            }
        ) {
            Text("Create Account")
        }
    }
}
