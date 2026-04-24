package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AnnouncementsViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val announcements by viewModel.announcements.collectAsState()
    val readIds by viewModel.readIds.collectAsState()
    val sessionManager = SessionManager(context)
    val myStudentNumber = sessionManager.fetchStudentNumber() ?: ""
    
    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (selectedAnnouncement == null) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF0F2F5)),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(announcements, key = { it.docId }) { announcement ->
                    val isRead = announcement.docId in readIds
                    AnnouncementCard(
                        announcement = announcement,
                        isRead = isRead,
                        myStudentNumber = myStudentNumber,
                        onLikeClick = { viewModel.toggleLike(announcement.docId) },
                        onClick = { 
                            selectedAnnouncement = announcement
                            viewModel.markAsRead(announcement.docId)
                        }
                    )
                }
            }
        } else {
            AnnouncementDetailView(
                announcement = selectedAnnouncement!!,
                viewModel = viewModel,
                myStudentNumber = myStudentNumber,
                modifier = Modifier.padding(padding),
                onBack = { selectedAnnouncement = null }
            )
        }
    }
}

@Composable
fun CategoryTag(category: String) {
    val backgroundColor = when (category.lowercase()) {
        "urgent" -> Color(0xFFFFEBEE)
        "event" -> Color(0xFFE3F2FD)
        "activity" -> Color(0xFFE8F5E9)
        "seminar" -> Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (category.lowercase()) {
        "urgent" -> Color(0xFFD32F2F)
        "event" -> Color(0xFF1976D2)
        "activity" -> Color(0xFF388E3C)
        "seminar" -> Color(0xFFF57C00)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = category.uppercase(),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 10.sp
        )
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement, 
    isRead: Boolean, 
    myStudentNumber: String,
    onLikeClick: () -> Unit,
    onClick: () -> Unit
) {
    val isLiked = announcement.likedBy.contains(myStudentNumber)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) MaterialTheme.colorScheme.surface 
                             else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (announcement.departmentName ?: "A").take(1),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = announcement.departmentName ?: "Campus Announcement",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (announcement.category.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CategoryTag(category = announcement.category)
                        }
                    }
                    Text(
                        text = SimpleDateFormat("MMM d 'at' h:mm a", Locale.getDefault()).format(Date(announcement.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (!isRead) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(8.dp)) {}
                }
            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announcement.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (announcement.imageUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = announcement.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    contentScale = ContentScale.FillWidth
                )
            }

            // Likes/Comments Count
            if (announcement.likedBy.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = announcement.likedBy.size.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InteractionButton(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, 
                    text = "Like",
                    tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onLikeClick
                )
                InteractionButton(
                    icon = Icons.Outlined.ChatBubbleOutline, 
                    text = "Comment",
                    onClick = onClick // Opens detail view
                )
            }
        }
    }
}

@Composable
fun InteractionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    text: String, 
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = tint)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDetailView(
    announcement: Announcement,
    viewModel: AnnouncementsViewModel,
    myStudentNumber: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val announcements by viewModel.announcements.collectAsState()
    val currentAnnouncement = announcements.find { it.docId == announcement.docId } ?: announcement
    val isLiked = currentAnnouncement.likedBy.contains(myStudentNumber)
    
    val comments by viewModel.getComments(announcement.docId).collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    val sessionManager = SessionManager(LocalContext.current)
    val myName = sessionManager.fetchUsername() ?: ""
    val myProfileImage = sessionManager.fetchProfileImageUrl()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, modifier = Modifier.imePadding()) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (replyingTo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Replying to ${replyingTo!!.studentName}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { replyingTo = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        if (myProfileImage != null) {
                            AsyncImage(
                                model = myProfileImage,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(myName.take(1).uppercase(), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write a comment...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF0F2F5),
                                unfocusedContainerColor = Color(0xFFF0F2F5),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (commentText.isNotBlank()) {
                                    viewModel.addComment(announcement.docId, commentText, replyingTo)
                                    commentText = ""
                                    replyingTo = null
                                }
                            })
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.addComment(announcement.docId, commentText, replyingTo)
                                    commentText = ""
                                    replyingTo = null
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            item {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Text((announcement.departmentName ?: "A").take(1), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(announcement.departmentName ?: "Campus Announcement", fontWeight = FontWeight.Bold)
                            if (announcement.category.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CategoryTag(category = announcement.category)
                            }
                        }
                        Text(
                            SimpleDateFormat("MMM d 'at' h:mm a", Locale.getDefault()).format(Date(announcement.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(announcement.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(announcement.content, style = MaterialTheme.typography.bodyLarge)
                }

                if (announcement.imageUrl != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = announcement.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        contentScale = ContentScale.FillWidth
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                
                // Interaction Stats
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentAnnouncement.likedBy.isNotEmpty()) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentAnnouncement.likedBy.size.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text("${comments.size} comments", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                }
                
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                
                // Interaction Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InteractionButton(
                        icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        text = "Like",
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { viewModel.toggleLike(announcement.docId) }
                    )
                    InteractionButton(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        text = "Comment",
                        onClick = { /* Focus text field */ }
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val topLevelComments = comments.filter { !it.isReply }
            items(topLevelComments, key = { it.docId }) { parentComment ->
                CommentHierarchy(
                    parentComment = parentComment,
                    allComments = comments,
                    myStudentNumber = myStudentNumber,
                    onReply = { replyingTo = it },
                    onReport = { reason -> viewModel.reportComment(parentComment.docId, reason) }
                )
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun CommentHierarchy(
    parentComment: Comment,
    allComments: List<Comment>,
    myStudentNumber: String,
    onReply: (Comment) -> Unit,
    onReport: (String) -> Unit
) {
    val replies = allComments.filter { 
        it.isReply && it.parentCommentId == parentComment.docId 
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        CommentItem(
            comment = parentComment,
            isMine = parentComment.studentNumber == myStudentNumber,
            onReply = { onReply(parentComment) },
            onReport = onReport
        )
        
        replies.forEach { reply ->
            Box(modifier = Modifier.padding(start = 44.dp)) {
                CommentItem(
                    comment = reply,
                    isMine = reply.studentNumber == myStudentNumber,
                    onReply = { onReply(reply) },
                    onReport = onReport,
                    isReply = true
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment, 
    isMine: Boolean, 
    onReply: () -> Unit, 
    onReport: (String) -> Unit,
    isReply: Boolean = false
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    
    val isAdmin = comment.role == "admin"
    val bubbleColor = if (isAdmin) Color(0xFFE3F2FD) else Color(0xFFF0F2F5)
    val nameColor = if (isAdmin) MaterialTheme.colorScheme.primary else Color.Unspecified

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (comment.profileImageUrl != null) {
            AsyncImage(
                model = comment.profileImageUrl,
                contentDescription = null,
                modifier = Modifier.size(if (isReply) 28.dp else 36.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(if (isReply) 28.dp else 36.dp), 
                shape = CircleShape, 
                color = if (isAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(comment.studentName.take(1).uppercase(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(bubbleColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.studentName, 
                        fontWeight = FontWeight.Bold, 
                        color = nameColor,
                        style = if (isReply) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Verified, 
                            contentDescription = "Admin", 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = comment.content, 
                    style = if (isReply) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(comment.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Reply", 
                    modifier = Modifier.clickable { onReply() }, 
                    fontWeight = FontWeight.Bold, 
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (!isMine) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Report", 
                        modifier = Modifier.clickable { showReportDialog = true }, 
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Comment") },
            text = {
                OutlinedTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    label = { Text("Reason for reporting") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onReport(reportReason)
                    showReportDialog = false
                }) { Text("Report") }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("Cancel") }
            }
        )
    }
}
