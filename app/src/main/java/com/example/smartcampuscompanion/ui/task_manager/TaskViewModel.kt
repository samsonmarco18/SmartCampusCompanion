package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.data.TaskRepository
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val repository: TaskRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val studentNumber = sessionManager.fetchStudentNumber() ?: ""

    val tasks: StateFlow<List<Task>> = repository.getTasksForStudent(studentNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(task: Task) = viewModelScope.launch {
        repository.insertTask(task.copy(studentNumber = studentNumber))
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(studentNumber = studentNumber))
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}
