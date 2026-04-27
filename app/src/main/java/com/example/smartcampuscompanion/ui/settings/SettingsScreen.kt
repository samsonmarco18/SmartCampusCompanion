package com.example.smartcampuscompanion.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var notificationsEnabled by remember { mutableStateOf(sessionManager.areNotificationsEnabled()) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        Toast.makeText(context, "Search feature coming soon", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Settings")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                UserProfileCard(
                    name = sessionManager.fetchUsername() ?: "User",
                    subtitle = "Student ID: ${sessionManager.fetchStudentNumber() ?: "N/A"}",
                    onEditClick = onNavigateToEditProfile
                )
            }

            item {
                SettingsGroup(title = "General") {
                    SettingsToggleItem(
                        title = "Notifications",
                        subtitle = "Campus updates and reminders",
                        icon = Icons.Default.Notifications,
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            notificationsEnabled = enabled
                            sessionManager.setNotificationsEnabled(enabled)
                            if (enabled) {
                                FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
                            } else {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("announcements")
                                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsToggleItem(
                        title = "Dark Mode",
                        subtitle = "System-wide dark theme",
                        icon = Icons.Default.Brightness4,
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                    SettingsClickableItem(
                        title = "Language",
                        subtitle = "English (US)",
                        icon = Icons.Default.Language,
                        onClick = { 
                            Toast.makeText(context, "Currently only English is supported", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            
            item {
                SettingsGroup(title = "Account") {
                    SettingsClickableItem(
                        title = "Edit Profile",
                        subtitle = "Personal info and photo",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToEditProfile
                    )
                    SettingsClickableItem(
                        title = "Security",
                        subtitle = "Password and biometric lock",
                        icon = Icons.Default.Lock,
                        onClick = { 
                            Toast.makeText(context, "Security settings coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SettingsClickableItem(
                        title = "Storage",
                        subtitle = "Manage offline data",
                        icon = Icons.Default.Storage,
                        onClick = { 
                            Toast.makeText(context, "Storage management coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                SettingsGroup(title = "Support") {
                    SettingsClickableItem(
                        title = "Help Center",
                        subtitle = "FAQs and support contact",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        onClick = { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/")) // Halimbawang support link
                            context.startActivity(intent)
                        }
                    )
                    SettingsClickableItem(
                        title = "Feedback",
                        subtitle = "Help us improve the app",
                        icon = Icons.Default.Feedback,
                        onClick = { 
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@smartcampus.com")
                                putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsClickableItem(
                        title = "About",
                        subtitle = "v1.0.0 (Stable Build)",
                        icon = Icons.Default.Info,
                        onClick = { 
                            Toast.makeText(context, "Smart Campus Companion v1.0.0", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout from Account")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Made with ♥ by Campus Tech",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout? You will need to sign in again to access your data.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun UserProfileCard(name: String, subtitle: String, onEditClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, fontSize = 12.sp) },
        leadingContent = { 
            Icon(
                icon, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            ) 
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                thumbContent = if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else null
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, fontSize = 12.sp) },
        leadingContent = { 
            Icon(
                icon, 
                contentDescription = null, 
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            ) 
        },
        trailingContent = { 
            Icon(
                Icons.Default.ChevronRight, 
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) 
        },
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
