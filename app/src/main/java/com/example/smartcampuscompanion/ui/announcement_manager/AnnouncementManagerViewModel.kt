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
            // Add announcement to Firestore
            val docRef = firestore.collection("announcements").add(announcement).await()
            
            // Note: Sending FCM notifications usually requires a server-side component (like Firebase Cloud Functions)
            // because you shouldn't expose your FCM Server Key in the Android app for security reasons.
            // For "everyone will be notified", the admin would send a message to the "announcements" topic.
            
            // To do this from the app for testing, you would normally call a Firebase Function or a backend API.
            // Since we're in a local/mock environment, we'll assume the topic subscription in MainActivity 
            // handles receiving when a tool sends to the "announcements" topic.
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
