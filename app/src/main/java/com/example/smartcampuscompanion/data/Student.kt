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
    var id: Int = 0,
    var studentNumber: String = "",
    var name: String = "",
    var password: String = "",
    var email: String = "",
    var yearLevel: String = "",
    var departmentName: String = "",
    var status: String = "Regular",
    var profileImageUrl: String? = null,
    var role: String = "student",
    var warningCount: Int = 0,
    var adminMessage: String? = null
)
