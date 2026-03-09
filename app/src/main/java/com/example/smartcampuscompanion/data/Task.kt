package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentNumber: String = "", // Default value to fix UI compilation and handle personalization in ViewModel
    val title: String,
    val description: String,
    val dueDate: Long
)
