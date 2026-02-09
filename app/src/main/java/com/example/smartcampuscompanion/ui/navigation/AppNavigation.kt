package com.example.smartcampuscompanion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsScreen
import com.example.smartcampuscompanion.ui.campus_info.CampusInfoScreen
import com.example.smartcampuscompanion.ui.campus_map.CampusMapScreen
import com.example.smartcampuscompanion.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanion.ui.grades.GradesScreen
import com.example.smartcampuscompanion.ui.login.LoginScreen
import com.example.smartcampuscompanion.ui.profile.ProfileScreen
import com.example.smartcampuscompanion.ui.task_manager.TaskManagerScreen
import com.example.smartcampuscompanion.util.SessionManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object CampusInfo : Screen("campus_info")
    object TaskManager : Screen("task_manager")
    object Announcements : Screen("announcements")
    object Grades : Screen("grades")
    object CampusMap : Screen("campus_map")
    object Profile : Screen("profile")
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
                onLogout = {
                    sessionManager.clearAuthToken()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToCampusInfo = { navController.navigate(Screen.CampusInfo.route) },
                onNavigateToSchedule = { navController.navigate(Screen.TaskManager.route) },
                onNavigateToGrades = { navController.navigate(Screen.Grades.route) },
                onNavigateToCampusMap = { navController.navigate(Screen.CampusMap.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Announcements.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.CampusInfo.route) {
            CampusInfoScreen(onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.TaskManager.route) {
            TaskManagerScreen(onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.Announcements.route) {
            AnnouncementsScreen()
        }
        composable(Screen.Grades.route) {
            GradesScreen()
        }
        composable(Screen.CampusMap.route) {
            CampusMapScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
