package com.example.smartcampuscompanion.data

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithGrades(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "studentNumber",
        entityColumn = "studentId"
    )
    val grades: List<Grade>
)
