package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "grades",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentNumber"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Grade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: String,
    val subject: String,
    val grade: Double
)
