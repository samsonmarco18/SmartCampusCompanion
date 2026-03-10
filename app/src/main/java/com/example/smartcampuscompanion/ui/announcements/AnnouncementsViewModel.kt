package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.Comment
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val announcementDao = database.announcementDao()
    private val commentDao = database.commentDao()
    private val sessionManager = SessionManager(application)

    val announcements: StateFlow<List<Announcement>> = announcementDao.getAnnouncementsForStudent(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAnnouncementsCount: StateFlow<Int> = announcementDao.getUnreadCount(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun getComments(announcementId: Int): Flow<List<Comment>> {
        return commentDao.getCommentsForAnnouncement(announcementId)
    }

    fun addComment(announcementId: Int, content: String) = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        val studentName = sessionManager.fetchUsername() ?: "Unknown"
        val comment = Comment(
            announcementId = announcementId,
            studentNumber = studentNumber,
            studentName = studentName,
            content = content
        )
        commentDao.insert(comment)
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber) {
            commentDao.delete(comment)
        }
    }

    fun updateComment(comment: Comment, newContent: String) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber) {
            commentDao.update(comment.copy(content = newContent))
        }
    }

    fun reportComment(commentId: Int) = viewModelScope.launch {
        commentDao.reportComment(commentId)
    }

    fun markAsRead(id: Int) = viewModelScope.launch {
        announcementDao.markAsRead(id)
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        announcementDao.delete(announcement)
    }
}
