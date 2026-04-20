package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.Comment
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnnouncementsScreen(
    onNavigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: AnnouncementsViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val announcements by viewModel.announcements.collectAsState()
    val sessionManager = remember { SessionManager(context) }
    val currentStudentNumber = sessionManager.fetchStudentNumber() ?: ""

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(announcements, key = { it.docId }) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        viewModel = viewModel,
                        currentStudentNumber = currentStudentNumber
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    viewModel: AnnouncementsViewModel,
    currentStudentNumber: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    val comments by viewModel.getComments(announcement.docId).collectAsState(initial = emptyList())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
                viewModel.markAsRead(announcement.docId)
            }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                CategoryChip(text = announcement.category)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(announcement.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (announcement.imageUrl != null) {
                        AsyncImage(
                            model = announcement.imageUrl,
                            contentDescription = "Announcement Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = announcement.content,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comments (${comments.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(onClick = { isExpanded = false }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse")
                        }
                    }

                    CommentSection(
                        announcementDocId = announcement.docId,
                        comments = comments,
                        viewModel = viewModel,
                        currentStudentNumber = currentStudentNumber
                    )
                }
            }
        }
    }
}

@Composable
fun CommentSection(
    announcementDocId: String,
    comments: List<Comment>,
    viewModel: AnnouncementsViewModel,
    currentStudentNumber: String
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        comments.forEach { comment ->
            CommentItem(
                comment = comment,
                isOwnComment = comment.studentNumber == currentStudentNumber,
                onDelete = { viewModel.deleteComment(comment) },
                onEdit = { viewModel.updateComment(comment, it) },
                onReport = { reason -> viewModel.reportComment(comment.docId, reason) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Add a comment...", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        viewModel.addComment(announcementDocId, newCommentText)
                        newCommentText = ""
                    }
                },
                enabled = newCommentText.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isOwnComment: Boolean,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    onReport: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(comment.content) }
    var showReportDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.studentName.take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
                )
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.studentName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(comment.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            if (isEditing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    trailingIcon = {
                        IconButton(onClick = { onEdit(editText); isEditing = false }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Text(
                    text = comment.content, 
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(16.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                if (isOwnComment) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { isEditing = true; showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { onDelete(); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Report") },
                        onClick = { showReportDialog = true; showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Flag, null) }
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onReport = { reason ->
                onReport(reason)
                showReportDialog = false
            }
        )
    }
}

@Composable
fun ReportDialog(onDismiss: () -> Unit, onReport: (String) -> Unit) {
    var reason by remember { mutableStateOf("") }
    val reasons = listOf("Spam", "Harassment", "Inappropriate content", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Comment") },
        text = {
            Column {
                Text("Select a reason for reporting this comment:")
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = r }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (reason == r), onClick = { reason = r })
                        Text(r, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                if (reason == "Other") {
                    var otherReason by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = otherReason,
                        onValueChange = { otherReason = it; reason = it },
                        placeholder = { Text("Specify reason...") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (reason.isNotBlank()) onReport(reason) },
                enabled = reason.isNotBlank()
            ) {
                Text("Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategoryChip(text: String) {
    val color = when (text) {
        "Urgent" -> MaterialTheme.colorScheme.error
        "Event" -> MaterialTheme.colorScheme.primary
        "Seminar" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
