package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var studentNumber: String = "",
    var title: String = "",
    var description: String = "",
    var dueDate: Long = 0,
    @get:Exclude @set:Exclude @Ignore var docId: String = ""
)
