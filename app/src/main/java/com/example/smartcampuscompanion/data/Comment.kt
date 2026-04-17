package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Announcement::class,
            parentColumns = ["id"],
            childColumns = ["announcementId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentNumber"],
            childColumns = ["studentNumber"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["announcementId"]), Index(value = ["studentNumber"])]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val announcementId: Int,
    val studentNumber: String,
    val studentName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isReported: Boolean = false,
    val reportedBy: String? = null,
    val reportReason: String? = null
)
