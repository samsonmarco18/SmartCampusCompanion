package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE studentNumber = :studentNumber ORDER BY dueDate ASC")
    fun getTasksForStudent(studentNumber: String): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
