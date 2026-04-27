package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "announcement_read_status",
    primaryKeys = ["studentNumber", "announcementId"],
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentNumber"],
            childColumns = ["studentNumber"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Announcement::class,
            parentColumns = ["id"],
            childColumns = ["announcementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["announcementId"])]
)
data class AnnouncementReadStatus(
    val studentNumber: String,
    val announcementId: Int
)
