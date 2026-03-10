package com.example.smartcampuscompanion.ui.announcement_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val announcementDao = AppDatabase.getDatabase(application).announcementDao()

    val announcements: StateFlow<List<Announcement>> = announcementDao.getAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(announcement: Announcement) = viewModelScope.launch {
        announcementDao.insert(announcement)
    }

    fun update(announcement: Announcement) = viewModelScope.launch {
        announcementDao.update(announcement)
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        announcementDao.delete(announcement)
    }
}
