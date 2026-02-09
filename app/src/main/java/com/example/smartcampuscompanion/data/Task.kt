package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["name"],
            childColumns = ["departmentName"],
            onDelete = ForeignKey.SET_NULL // If a department is deleted, set the task's department to null
        )
    ],
    indices = [Index(value = ["departmentName"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long,
    val departmentName: String? // Nullable to allow for tasks not assigned to any department
)
