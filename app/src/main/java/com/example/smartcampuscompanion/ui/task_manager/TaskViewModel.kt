package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val sessionManager = SessionManager(application)
    private val studentNumber = sessionManager.fetchStudentNumber() ?: ""

    val tasks: StateFlow<List<Task>> = taskDao.getTasksForStudent(studentNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(task: Task) = viewModelScope.launch {
        taskDao.insert(task.copy(studentNumber = studentNumber))
    }

    fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task.copy(studentNumber = studentNumber))
    }

    fun delete(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
    }
}
