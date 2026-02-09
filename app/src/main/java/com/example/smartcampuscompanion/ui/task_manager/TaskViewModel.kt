package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = AppDatabase.getDatabase(application).taskDao()

    val tasks = taskDao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
    }
}
