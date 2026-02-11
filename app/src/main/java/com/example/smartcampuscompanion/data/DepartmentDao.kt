package com.example.smartcampuscompanion.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<Department>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<Grade>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM departments")
    suspend fun clearDepartments()

    @Query("DELETE FROM students")
    suspend fun clearStudents()

    @Query("DELETE FROM grades")
    suspend fun clearGrades()

    @Transaction
    suspend fun prePopulate() {
        clearDepartments()
        clearStudents()
        clearGrades()
    }

    @Transaction
    @Query("SELECT * FROM departments")
    fun getDepartmentsWithStudents(): Flow<List<DepartmentWithStudents>>
}
