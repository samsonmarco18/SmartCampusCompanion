package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksForStudent(studentNumber: String): Flow<List<Task>> = 
        taskDao.getTasksForStudent(studentNumber)

    suspend fun insertTask(task: Task) = taskDao.insert(task)

    suspend fun updateTask(task: Task) = taskDao.update(task)

    suspend fun deleteTask(task: Task) = taskDao.delete(task)
}
