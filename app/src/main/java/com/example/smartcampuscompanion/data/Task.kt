package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var studentNumber: String = "",
    var title: String = "",
    var description: String = "",
    var startDate: Long = 0,
    var dueDate: Long = 0,
    var isSynced: Boolean = true,
    var lastModified: Long = System.currentTimeMillis(),
    @get:Exclude @set:Exclude var docId: String = ""
)
