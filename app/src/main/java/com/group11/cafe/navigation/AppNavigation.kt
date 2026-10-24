package com.group11.cafe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.group11.cafe.ui.auth.LoginScreen
import com.group11.cafe.ui.dashboard.DashboardScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {

            LoginScreen(
                onLoginClick = {
                    navController.navigate(
                        Screen.Dashboard.route
                    )
                },
                onGuestClick = {
                    navController.navigate(
                        Screen.Dashboard.route
                    )
                }
            )
        }

        composable(Screen.Dashboard.route) {

            DashboardScreen()
        }
    }
}