package com.example.smartcampuscompanion.ui.announcements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.Announcement
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnnouncementsScreen(
    onNavigateUp: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AnnouncementsViewModel = viewModel()
) {
    val announcements by viewModel.announcements.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Announcements") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No announcements yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
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
    // If it's NOT read, we use grey. If it IS read, we use the type-specific colors.
    val backgroundColor = if (!announcement.isRead) {
        Color(0xFFF5F5F5) // Grey for unread
    } else {
        when (announcement.type) {
            "ADD" -> Color(0xFFE8F5E9) // Light Green
            "UPDATE" -> Color(0xFFFFFDE7) // Light Yellow
            "DELETE" -> Color(0xFFFFEBEE) // Light Red
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    val contentColor = if (!announcement.isRead) {
        Color.Gray
    } else {
        when (announcement.type) {
            "ADD" -> Color(0xFF2E7D32) // Dark Green
            "UPDATE" -> Color(0xFFFBC02D) // Dark Yellow
            "DELETE" -> Color(0xFFC62828) // Dark Red
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
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
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(announcement.timestamp)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (!announcement.isRead) Color.Black else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(announcement.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
