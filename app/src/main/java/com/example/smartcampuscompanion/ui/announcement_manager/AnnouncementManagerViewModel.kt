package com.example.smartcampuscompanion.ui.announcement_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AnnouncementManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    init {
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        firestore.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _announcements.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Announcement::class.java)?.apply {
                            docId = doc.id
                        }
                    }
                }
            }
    }

    fun insert(announcement: Announcement) = viewModelScope.launch {
        try {
            firestore.collection("announcements").add(announcement).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun update(announcement: Announcement) = viewModelScope.launch {
        if (announcement.docId.isNotEmpty()) {
            try {
                firestore.collection("announcements").document(announcement.docId).set(announcement).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun delete(announcement: Announcement) = viewModelScope.launch {
        if (announcement.docId.isNotEmpty()) {
            try {
                firestore.collection("announcements").document(announcement.docId).delete().await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
