package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val type: String, // "ADD", "UPDATE", "DELETE"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
