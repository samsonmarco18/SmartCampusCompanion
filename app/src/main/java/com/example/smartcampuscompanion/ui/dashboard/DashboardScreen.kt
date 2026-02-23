package com.example.smartcampuscompanion.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsViewModel
import com.example.smartcampuscompanion.ui.campus_info.CampusViewModel
import com.example.smartcampuscompanion.ui.navigation.Screen
import kotlinx.coroutines.launch

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val badgeCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToCampusInfo: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAnnouncementManager: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStudentRecord: () -> Unit,
    campusViewModel: CampusViewModel = viewModel(),
    announcementsViewModel: AnnouncementsViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val departmentsWithStudents by campusViewModel.departmentsWithStudents.collectAsState()
    val totalDepartments = departmentsWithStudents.size
    val totalStudents = departmentsWithStudents.sumOf { it.students.size }

    val unreadCount by announcementsViewModel.unreadAnnouncementsCount.collectAsState()

    val items = listOf(
        DashboardItem("Campus Info", Icons.Default.Info, onNavigateToCampusInfo),
        DashboardItem("Schedule", Icons.Default.CalendarMonth, onNavigateToSchedule),
        DashboardItem("Student Record", Icons.Default.School, onNavigateToStudentRecord),
        DashboardItem("Announcement Manager", Icons.Default.Announcement, onNavigateToAnnouncementManager),
        DashboardItem("Announcements", Icons.Default.Notifications, onNavigateToNotifications, badgeCount = unreadCount),
        DashboardItem("Profile", Icons.Default.Person, onNavigateToProfile),
    )

    val navigationItems = listOf(
        Screen.CampusInfo,
        Screen.TaskManager,
        Screen.StudentRecord,
        Screen.AnnouncementManager,
        Screen.Announcements,
        Screen.Profile,
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Smart Campus",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                navigationItems.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            when (screen) {
                                Screen.CampusInfo -> onNavigateToCampusInfo()
                                Screen.TaskManager -> onNavigateToSchedule()
                                Screen.StudentRecord -> onNavigateToStudentRecord()
                                Screen.AnnouncementManager -> onNavigateToAnnouncementManager()
                                Screen.Announcements -> onNavigateToNotifications()
                                Screen.Profile -> onNavigateToProfile()
                                else -> {}
                            }
                        },
                        icon = {
                            when (screen) {
                                Screen.CampusInfo -> Icon(Icons.Default.Info, null)
                                Screen.TaskManager -> Icon(Icons.Default.CalendarMonth, null)
                                Screen.StudentRecord -> Icon(Icons.Default.School, null)
                                Screen.AnnouncementManager -> Icon(Icons.Default.Announcement, null)
                                Screen.Announcements -> BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge {
                                                Text(unreadCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, null)
                                }
                                Screen.Profile -> Icon(Icons.Default.Person, null)
                                else -> {}
                            }
                        }
                    )
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        onNavigateToSettings()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Settings, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { onLogout() },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Welcome to your Smart Campus Companion!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatisticCard(
                                title = "Total Departments",
                                value = totalDepartments.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatisticCard(
                                title = "Total Students",
                                value = totalStudents.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Services",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(items) { item ->
                    DashboardCard(item)
                }
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(item: DashboardItem) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { item.onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BadgedBox(
                badge = {
                    if (item.badgeCount > 0) {
                        Badge {
                            Text(item.badgeCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
