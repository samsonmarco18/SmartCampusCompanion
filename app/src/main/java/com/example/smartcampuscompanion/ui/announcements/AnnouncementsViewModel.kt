package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.*
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class AnnouncementsViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val sessionManager = SessionManager(application)

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _readIds = MutableStateFlow<Set<String>>(emptySet())
    val readIds: StateFlow<Set<String>> = _readIds

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val unreadAnnouncementsCount: StateFlow<Int> = combine(_announcements, _readIds) { announcements, readIds ->
        announcements.count { it.docId !in readIds }
    }.let { flow ->
        val stateFlow = MutableStateFlow(0)
        viewModelScope.launch {
            flow.collect { stateFlow.value = it }
        }
        stateFlow
    }

    private var isFirstFetch = true

    init {
        fetchAnnouncements()
        observeReadStatus()
    }

    private fun fetchAnnouncements() {
        val studentNumber = sessionManager.fetchStudentNumber() ?: ""
        _isLoading.value = true
        firestore.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                _isLoading.value = false
                if (e != null) {
                    Log.e("Firestore", "Error fetching announcements", e)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Announcement::class.java)?.apply {
                            docId = doc.id
                        }
                    }
                    val filtered = list.filter { it.studentNumber == null || it.studentNumber == studentNumber }
                    
                    // Check for new announcements to trigger a local notification
                    if (!isFirstFetch && filtered.isNotEmpty()) {
                        val latestNew = filtered.first()
                        val currentList = _announcements.value
                        if (currentList.isNotEmpty() && latestNew.timestamp > currentList.first().timestamp) {
                            showLocalNotification(latestNew)
                        }
                    }
                    
                    _announcements.value = filtered
                    isFirstFetch = false
                }
            }
    }

    private fun showLocalNotification(announcement: Announcement) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "campus_notifications_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Campus Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Announcement: ${announcement.title}")
            .setContentText(announcement.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(announcement.timestamp.toInt(), builder.build())
    }

    private fun observeReadStatus() {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return
        firestore.collection("announcement_read_status")
            .whereEqualTo("studentNumber", studentNumber)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val ids = snapshot.documents.mapNotNull { it.getString("announcementDocId") }.toSet()
                    _readIds.value = ids
                }
            }
    }

    fun toggleLike(announcementId: String) = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        val docRef = firestore.collection("announcements").document(announcementId)
        
        try {
            val doc = docRef.get().await()
            val likedBy = doc.get("likedBy") as? List<*> ?: emptyList<String>()
            
            if (likedBy.contains(studentNumber)) {
                docRef.update("likedBy", FieldValue.arrayRemove(studentNumber)).await()
            } else {
                docRef.update("likedBy", FieldValue.arrayUnion(studentNumber)).await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error toggling like", e)
        }
    }

    fun getComments(announcementDocId: String): Flow<List<Comment>> = callbackFlow {
        // We remove orderBy from the query to avoid the need for a composite index in Firestore.
        // This ensures the listener works immediately and stays real-time even without manual index creation.
        val subscription = firestore.collection("comments")
            .whereEqualTo("announcementDocId", announcementDocId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Error fetching comments for $announcementDocId", e)
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Comment::class.java)?.apply {
                            docId = doc.id
                        }
                    }.sortedBy { it.timestamp } // Sort locally to maintain order
                    trySend(comments)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun addComment(announcementDocId: String, content: String, replyTo: Comment? = null) = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        val studentName = sessionManager.fetchUsername() ?: "Unknown"
        val role = sessionManager.fetchRole() ?: "student"
        val profileImageUrl = sessionManager.fetchProfileImageUrl()
        
        val comment = Comment(
            announcementDocId = announcementDocId,
            studentNumber = studentNumber,
            studentName = studentName,
            profileImageUrl = profileImageUrl,
            content = content,
            role = role,
            timestamp = System.currentTimeMillis(),
            isReply = replyTo != null,
            parentCommentId = replyTo?.docId,
            replyToName = replyTo?.studentName,
            replyToStudentNumber = replyTo?.studentNumber
        )
        
        try {
            firestore.collection("comments").add(comment).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding comment", e)
        }
    }

    fun markAsRead(announcementDocId: String) = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        try {
            val existing = firestore.collection("announcement_read_status")
                .whereEqualTo("studentNumber", studentNumber)
                .whereEqualTo("announcementDocId", announcementDocId)
                .get()
                .await()
            
            if (existing.isEmpty) {
                val status = hashMapOf(
                    "studentNumber" to studentNumber,
                    "announcementDocId" to announcementDocId
                )
                firestore.collection("announcement_read_status").add(status).await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error marking as read", e)
        }
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber && comment.docId.isNotEmpty()) {
            try {
                firestore.collection("comments").document(comment.docId).delete().await()
            } catch (e: Exception) {
                Log.e("Firestore", "Error deleting comment", e)
            }
        }
    }

    fun updateComment(comment: Comment, newContent: String) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber && comment.docId.isNotEmpty()) {
            try {
                firestore.collection("comments").document(comment.docId)
                    .update("content", newContent).await()
            } catch (e: Exception) {
                Log.e("Firestore", "Error updating comment", e)
            }
        }
    }

    fun reportComment(commentId: String, reason: String) = viewModelScope.launch {
        val reporterName = sessionManager.fetchUsername() ?: "Anonymous"
        try {
            firestore.collection("comments").document(commentId).update(
                mapOf(
                    "isReported" to true,
                    "reportedBy" to reporterName,
                    "reportReason" to reason
                )
            ).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error reporting comment", e)
        }
    }

    val reportedComments: Flow<List<Comment>> = callbackFlow {
        val subscription = firestore.collection("comments")
            .whereEqualTo("isReported", true)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Comment::class.java)?.apply {
                            docId = doc.id
                        }
                    }
                    trySend(comments)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun dismissReport(commentId: String) = viewModelScope.launch {
        try {
            firestore.collection("comments").document(commentId).update(
                mapOf(
                    "isReported" to false,
                    "reportedBy" to null,
                    "reportReason" to null
                )
            ).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error dismissing report", e)
        }
    }

    fun warnUser(studentNumber: String) = viewModelScope.launch {
        try {
            val userQuery = firestore.collection("users")
                .whereEqualTo("studentNumber", studentNumber)
                .get()
                .await()
            
            val userDoc = userQuery.documents.firstOrNull()
            if (userDoc != null) {
                val currentWarnings = userDoc.getLong("warningCount") ?: 0
                val newWarnings = currentWarnings + 1
                val updates = hashMapOf<String, Any>(
                    "warningCount" to newWarnings,
                    "adminMessage" to "You have been warned. Total warnings: $newWarnings. Please follow the community guidelines.",
                    "status" to if (newWarnings >= 3) "Banned" else userDoc.getString("status") ?: "Regular"
                )
                userDoc.reference.update(updates).await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error warning user", e)
        }
    }

    fun banUser(studentNumber: String) = viewModelScope.launch {
        try {
            val userQuery = firestore.collection("users")
                .whereEqualTo("studentNumber", studentNumber)
                .get()
                .await()
            
            val userDoc = userQuery.documents.firstOrNull()
            if (userDoc != null) {
                val updates = hashMapOf<String, Any>(
                    "status" to "Banned",
                    "adminMessage" to "Your account has been banned due to violations of our community guidelines."
                )
                userDoc.reference.update(updates).await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error banning user", e)
        }
    }

    fun clearAdminMessage() = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        try {
            val userQuery = firestore.collection("users")
                .whereEqualTo("studentNumber", studentNumber)
                .get()
                .await()
            
            userQuery.documents.firstOrNull()?.reference?.update("adminMessage", null)?.await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error clearing admin message", e)
        }
    }
}
