package com.example.smartcampuscompanion.data

import androidx.room.Embedded
import androidx.room.Relation

data class DepartmentWithStudents(
    @Embedded val department: Department,
    @Relation(
        parentColumn = "name",
        entityColumn = "departmentName"
    )
    val students: List<Student>
)
