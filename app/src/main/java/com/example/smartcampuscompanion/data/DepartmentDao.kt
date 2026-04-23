package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {

    @Transaction
    @Query("SELECT * FROM departments")
    fun getDepartmentsWithStudents(): Flow<List<DepartmentWithStudents>>

    @Query("SELECT * FROM departments")
    suspend fun getAllDepartmentsSync(): List<Department>

    @Query("SELECT * FROM students WHERE name = :name LIMIT 1")
    suspend fun getStudentByName(name: String): Student?

    @Query("SELECT * FROM students WHERE studentNumber = :studentNumber LIMIT 1")
    suspend fun getStudentByStudentNumber(studentNumber: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<Department>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}