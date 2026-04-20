package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

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
    var id: Int = 0,
    var announcementId: Int = 0,
    var announcementDocId: String = "",
    var studentNumber: String = "",
    var studentName: String = "",
    var content: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var isReported: Boolean = false,
    var reportedBy: String? = null,
    var reportReason: String? = null
) {
    @get:Exclude
    @set:Exclude
    @Ignore
    var docId: String = ""
}
