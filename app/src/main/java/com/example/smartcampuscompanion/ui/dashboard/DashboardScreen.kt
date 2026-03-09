package com.example.smartcampuscompanion.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsViewModel
import com.example.smartcampuscompanion.ui.campus_info.CampusViewModel
import com.example.smartcampuscompanion.ui.navigation.Screen
import com.example.smartcampuscompanion.ui.theme.BeigeBackground
import com.example.smartcampuscompanion.ui.theme.BeigePrimary
import com.example.smartcampuscompanion.ui.theme.BeigeSecondary
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
                Column {
                    TopAppBar(
                        title = { Text("Dashboard", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = onLogout) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BeigePrimary
                        )
                    )
                    // Dual-color header style from Student Record
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BeigeSecondary)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Departments: $totalDepartments",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        VerticalDivider(
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.height(16.dp),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Text(
                            text = "Total Students: $totalStudents",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            containerColor = BeigeBackground
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "Welcome to Smart Campus!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BeigeSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                item {
                    SectionHeader("Available Services")
                }

                items(items) { item ->
                    DashboardServiceCard(item)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(name: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = BeigePrimary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 16.dp)
                    .background(BeigePrimary, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BeigePrimary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardServiceCard(item: DashboardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = BeigeBackground
                ) {
                    Box(contentAlignment = Alignment.Center) {
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
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = BeigePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(48.dp)
                    .background(BeigePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
