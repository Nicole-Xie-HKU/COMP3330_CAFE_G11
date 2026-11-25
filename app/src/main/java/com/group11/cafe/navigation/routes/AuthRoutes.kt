package com.group11.cafe.navigation.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.group11.cafe.navigation.Screen
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.auth.RegisterScreen
import com.group11.cafe.viewmodel.LoginViewModel
import com.group11.cafe.viewmodel.RegisterViewModel

@Composable
fun LoginRoute(navController: NavHostController) {
    val loginViewModel: LoginViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LoginScreen(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onLoginClick = { email, password ->
            if (email.isBlank() || password.isBlank()) {
                errorMessage = "Enter your email and password."
                return@LoginScreen
            }

            isLoading = true
            errorMessage = null

            loginViewModel.login(
                email.trim(),
                password,
                onSuccess = {
                    isLoading = false
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onFailure = {
                    isLoading = false
                    errorMessage = it.message ?: "Login failed."
                }
            )
        },
        onGuestClick = {
            isLoading = true
            errorMessage = null

            loginViewModel.loginAsGuest(
                onSuccess = {
                    isLoading = false
                    navController.navigate(Screen.ParticipantDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onFailure = {
                    isLoading = false
                    errorMessage = it.message ?: "Guest login failed."
                }
            )
        },
        onRegisterClick = {
            navController.navigate(Screen.Register.route)
        }
    )
}

@Composable
fun RegisterRoute(navController: NavHostController) {
    val registerViewModel: RegisterViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    RegisterScreen(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRegisterClick = { name, email, password, role ->
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                errorMessage = "Enter your name, email and password."
                return@RegisterScreen
            }

            isLoading = true
            errorMessage = null

            registerViewModel.register(
                name.trim(),
                email.trim(),
                password,
                role,
                onSuccess = {
                    isLoading = false
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onFailure = {
                    isLoading = false
                    errorMessage = it.message ?: "Registration failed."
                }
            )
        }
    )
}

fun logout(navController: NavHostController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate(Screen.Login.route) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }
}
