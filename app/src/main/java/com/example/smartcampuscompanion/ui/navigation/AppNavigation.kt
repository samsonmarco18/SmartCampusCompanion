package com.example.smartcampuscompanion.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsScreen
import com.example.smartcampuscompanion.ui.campus_info.CampusInfoScreen
import com.example.smartcampuscompanion.ui.campus_info.CampusViewModel
import com.example.smartcampuscompanion.ui.dashboard.DashboardScreen
import com.example.smartcampuscompanion.ui.login.LoginScreen
import com.example.smartcampuscompanion.ui.profile.ProfileScreen
import com.example.smartcampuscompanion.ui.settings.SettingsScreen
import com.example.smartcampuscompanion.ui.signup.SignUpScreen
import com.example.smartcampuscompanion.ui.student_record.StudentRecordScreen
import com.example.smartcampuscompanion.ui.task_manager.TaskManagerScreen
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.ui.announcement_manager.AnnouncementManagerScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object CampusInfo : Screen("campus_info")
    object TaskManager : Screen("task_manager")
    object Announcements : Screen("announcements")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object StudentRecord : Screen("student_record")
    object AnnouncementManager : Screen("announcement_manager")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()
    val campusViewModel: CampusViewModel = viewModel()
    val startDestination = if (sessionManager.fetchUsername() != null) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    val onLogout = {
        sessionManager.clearSession()
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { // Clear entire backstack on logout
                inclusive = true
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(onSignUpSuccess = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.SignUp.route) {
                        inclusive = true
                    }
                }
            }, onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.SignUp.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogout = onLogout,
                onNavigateToCampusInfo = { navController.navigate(Screen.CampusInfo.route) },
                onNavigateToSchedule = { navController.navigate(Screen.TaskManager.route) },
                onNavigateToAnnouncementManager = { navController.navigate(Screen.AnnouncementManager.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Announcements.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStudentRecord = { navController.navigate(Screen.StudentRecord.route) },
                campusViewModel = campusViewModel
            )
        }
        composable(Screen.CampusInfo.route) {
            CampusInfoScreen(viewModel = campusViewModel, onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.TaskManager.route) {
            TaskManagerScreen(onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.Announcements.route) {
            AnnouncementsScreen(onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateUp = { navController.navigateUp() },
                onLogout = onLogout
            )
        }
        composable(Screen.StudentRecord.route) {
            StudentRecordScreen(onBackClick = { navController.navigateUp() })
        }
        composable(Screen.AnnouncementManager.route) {
            AnnouncementManagerScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}
