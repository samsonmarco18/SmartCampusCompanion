package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AnnouncementRepository
import com.example.smartcampuscompanion.data.Comment
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementsViewModel(
    application: Application,
    private val repository: AnnouncementRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    val announcements: StateFlow<List<Announcement>> = repository.getAnnouncementsForStudent(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAnnouncementsCount: StateFlow<Int> = repository.getUnreadCount(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun getComments(announcementId: Int): Flow<List<Comment>> {
        return repository.getCommentsForAnnouncement(announcementId)
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
        repository.insertComment(comment)
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber) {
            repository.deleteComment(comment)
        }
    }

    fun updateComment(comment: Comment, newContent: String) = viewModelScope.launch {
        val currentStudentNumber = sessionManager.fetchStudentNumber()
        if (comment.studentNumber == currentStudentNumber) {
            repository.updateComment(comment.copy(content = newContent))
        }
    }

    fun reportComment(commentId: Int) = viewModelScope.launch {
        repository.reportComment(commentId)
    }

    fun markAsRead(id: Int) = viewModelScope.launch {
        repository.markAsRead(id)
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        repository.deleteAnnouncement(announcement)
    }
}
