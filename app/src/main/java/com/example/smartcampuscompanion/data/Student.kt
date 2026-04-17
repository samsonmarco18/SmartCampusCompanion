package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["name"],
            childColumns = ["departmentName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["departmentName"]),
        Index(value = ["studentNumber"], unique = true)
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentNumber: String,
    val name: String,
    val password: String,
    val email: String,
    val yearLevel: String,
    val departmentName: String,
    val status: String = "Regular",
    val profileImageUrl: String? = null,
    val role: String = "student",
    val warningCount: Int = 0,
    val adminMessage: String? = null
)
