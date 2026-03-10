package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "Event", "Activity", "Urgent", "Seminar"
    val departmentName: String? = null,
    val dueDate: Long,
    val studentNumber: String? = null,
    val imageUrl: String? = null, // Path or URI to the image
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
