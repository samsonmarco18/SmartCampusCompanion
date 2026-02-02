package com.example.smartcampuscompanion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.ui.campus_info.CampusInfoScreen
import com.example.smartcampuscompanion.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanion.ui.login.LoginScreen
import com.example.smartcampuscompanion.util.SessionManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object CampusInfo : Screen("campus_info")
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()
    val startDestination = if (sessionManager.fetchAuthToken() != null) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCampusInfo = { navController.navigate(Screen.CampusInfo.route) },
                onLogout = {
                    sessionManager.clearAuthToken()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.CampusInfo.route) {
            CampusInfoScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}
