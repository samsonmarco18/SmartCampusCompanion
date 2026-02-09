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
    indices = [Index(value = ["departmentName"])]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val yearLevel: String,
    val departmentName: String
)
