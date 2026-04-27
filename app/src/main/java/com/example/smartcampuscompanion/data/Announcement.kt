package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var category: String = "", // "Event", "Activity", "Urgent", "Seminar"
    var departmentName: String? = null,
    var dueDate: Long = 0,
    var studentNumber: String? = null,
    var imageUrl: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var likedBy: List<String> = emptyList() // List of student numbers who liked
) {
    @get:Exclude
    @set:Exclude
    @Ignore
    var docId: String = ""
}
