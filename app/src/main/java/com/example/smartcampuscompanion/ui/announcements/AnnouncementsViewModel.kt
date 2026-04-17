package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.*
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
    private val departmentDao = database.departmentDao()
    private val sessionManager = SessionManager(application)

    val announcements: StateFlow<List<AnnouncementWithReadStatus>> = announcementDao.getAnnouncementsForStudent(sessionManager.fetchStudentNumber() ?: "")
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

    fun reportComment(commentId: Int, reason: String) = viewModelScope.launch {
        val reporterName = sessionManager.fetchUsername() ?: "Anonymous"
        commentDao.reportComment(commentId, reporterName, reason)
    }

    fun markAsRead(id: Int) = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        announcementDao.markAsRead(AnnouncementReadStatus(studentNumber, id))
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        announcementDao.delete(announcement)
    }

    // Admin actions for reported comments
    val reportedComments: Flow<List<Comment>> = commentDao.getReportedComments()

    fun dismissReport(commentId: Int) = viewModelScope.launch {
        commentDao.dismissReport(commentId)
    }

    fun warnUser(studentNumber: String) = viewModelScope.launch {
        val student = departmentDao.getStudentByStudentNumber(studentNumber)
        student?.let {
            val newWarningCount = it.warningCount + 1
            val updatedStudent = it.copy(
                warningCount = newWarningCount,
                adminMessage = "You have been warned. Total warnings: ${newWarningCount}. Please follow the community guidelines.",
                status = if (newWarningCount >= 3) "Banned" else it.status
            )
            departmentDao.updateStudent(updatedStudent)
        }
    }

    fun banUser(studentNumber: String) = viewModelScope.launch {
        val student = departmentDao.getStudentByStudentNumber(studentNumber)
        student?.let {
            departmentDao.updateStudent(it.copy(
                status = "Banned",
                adminMessage = "Your account has been banned due to multiple violations or a severe violation of our community guidelines."
            ))
        }
    }

    fun clearAdminMessage() = viewModelScope.launch {
        val studentNumber = sessionManager.fetchStudentNumber() ?: return@launch
        val student = departmentDao.getStudentByStudentNumber(studentNumber)
        student?.let {
            departmentDao.updateStudent(it.copy(adminMessage = null))
        }
    }
}
