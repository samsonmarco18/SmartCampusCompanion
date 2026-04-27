package com.example.smartcampuscompanion.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "departments")
data class Department(
    @PrimaryKey val name: String = "",
    val officeEmail: String = ""
)

data class DepartmentWithStudents(
    @Embedded val department: Department,
    @Relation(
        entity = Student::class,
        parentColumn = "name",
        entityColumn = "departmentName"
    )
    val students: List<StudentWithGrades>
)
