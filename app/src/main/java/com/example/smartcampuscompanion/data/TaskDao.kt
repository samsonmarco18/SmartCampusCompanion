package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE studentNumber = :studentNumber ORDER BY dueDate ASC")
    fun getTasksForStudent(studentNumber: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE studentNumber = :studentNumber AND isSynced = 0")
    suspend fun getUnsyncedTasks(studentNumber: String): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
    
    @Query("DELETE FROM tasks WHERE studentNumber = :studentNumber")
    suspend fun deleteTasksForStudent(studentNumber: String)

    @Query("SELECT * FROM tasks WHERE docId = :docId LIMIT 1")
    suspend fun getTaskByDocId(docId: String): Task?
}
