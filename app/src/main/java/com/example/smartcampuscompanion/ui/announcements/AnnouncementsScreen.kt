package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnnouncementsScreen(
    onNavigateUp: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AnnouncementsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val announcements by viewModel.announcements.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Announcements", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (announcements.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No announcements yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(announcements) { announcement ->
                    AnnouncementItem(
                        announcement = announcement,
                        onClick = { viewModel.markAsRead(announcement.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementItem(
    announcement: Announcement,
    onClick: () -> Unit
) {
    val backgroundColor = if (announcement.isRead) {
        MaterialTheme.colorScheme.surface
    } else {
        when (announcement.category) {
            "Event" -> Color(0xFFE8F5E9) // Light Green (keeping for contrast, but could use surface)
            "Activity" -> Color(0xFFFFFDE7) // Light Yellow
            "Urgent" -> Color(0xFFFFEBEE) // Light Red
            "Seminar" -> Color(0xFFE1F5FE) // Light Blue
            else -> MaterialTheme.colorScheme.surface
        }
    }

    val contentColor = if (announcement.isRead) {
        Color.Gray
    } else {
        when (announcement.category) {
            "Event" -> Color(0xFF2E7D32) // Dark Green
            "Activity" -> Color(0xFFFBC02D) // Dark Yellow
            "Urgent" -> Color(0xFFC62828) // Dark Red
            "Seminar" -> Color(0xFF0277BD) // Dark Blue
            else -> MaterialTheme.colorScheme.secondary
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (!announcement.isRead) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                )
                ChipView(text = announcement.category)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (!announcement.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(announcement.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(announcement.timestamp)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

        }
    }
}

@Composable
fun ChipView(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
    }
}
