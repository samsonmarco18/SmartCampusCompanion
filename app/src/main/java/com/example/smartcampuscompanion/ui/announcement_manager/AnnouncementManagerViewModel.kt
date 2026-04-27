package com.example.smartcampuscompanion.ui.announcement_manager

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

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

    private suspend fun processImageToBase64(uriString: String): String? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            // If already base64 or web url, return as is
            if (uriString.startsWith("data:image") || uriString.startsWith("http")) return@withContext uriString
            
            val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Resize to keep Base64 string within Firestore's 1MB limit (aim for < 500KB)
            val resizedBitmap = resizeBitmap(bitmap, 800) 
            
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun resizeBitmap(source: Bitmap, maxWidth: Int): Bitmap {
        if (source.width <= maxWidth) return source
        val aspectRatio = source.height.toDouble() / source.width.toDouble()
        val targetHeight = (maxWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(source, maxWidth, targetHeight, true)
    }

    fun insert(announcement: Announcement) = viewModelScope.launch {
        try {
            val finalAnnouncement = if (!announcement.imageUrl.isNullOrBlank()) {
                val base64Image = processImageToBase64(announcement.imageUrl!!)
                announcement.copy(imageUrl = base64Image)
            } else announcement
            
            firestore.collection("announcements").add(finalAnnouncement).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun update(announcement: Announcement) = viewModelScope.launch {
        if (announcement.docId.isNotEmpty()) {
            try {
                val finalAnnouncement = if (!announcement.imageUrl.isNullOrBlank()) {
                    val base64Image = processImageToBase64(announcement.imageUrl!!)
                    announcement.copy(imageUrl = base64Image)
                } else announcement
                
                firestore.collection("announcements").document(announcement.docId).set(finalAnnouncement).await()
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
