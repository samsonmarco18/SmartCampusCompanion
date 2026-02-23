package com.example.smartcampuscompanion.ui.announcements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementsViewModel(application: Application) : AndroidViewModel(application) {

    private val announcementDao = AppDatabase.getDatabase(application).announcementDao()
    private val sessionManager = SessionManager(application)

    val announcements: StateFlow<List<Announcement>> = announcementDao.getAnnouncementsForStudent(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAnnouncementsCount: StateFlow<Int> = announcementDao.getUnreadCount(sessionManager.fetchStudentNumber() ?: "")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(id: Int) = viewModelScope.launch {
        announcementDao.markAsRead(id)
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        announcementDao.delete(announcement)
    }
}
