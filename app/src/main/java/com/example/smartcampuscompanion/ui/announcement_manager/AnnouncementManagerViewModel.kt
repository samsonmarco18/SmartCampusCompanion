package com.example.smartcampuscompanion.ui.announcement_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AnnouncementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementManagerViewModel(
    application: Application,
    private val repository: AnnouncementRepository
) : AndroidViewModel(application) {

    val announcements: StateFlow<List<Announcement>> = repository.getAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(announcement: Announcement) = viewModelScope.launch {
        repository.insertAnnouncement(announcement)
    }

    fun update(announcement: Announcement) = viewModelScope.launch {
        repository.updateAnnouncement(announcement)
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        repository.deleteAnnouncement(announcement)
    }
}
