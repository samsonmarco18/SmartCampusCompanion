package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskDao = database.taskDao()
    private val announcementDao = database.announcementDao()

    private val _selectedDepartment = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = _selectedDepartment.flatMapLatest { department ->
        if (department == null) {
            taskDao.getAllTasks()
        } else {
            taskDao.getTasksByDepartment(department)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDepartmentFilter(departmentName: String?) {
        _selectedDepartment.value = departmentName
    }

    fun insert(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        announcementDao.insert(
            Announcement(
                title = "New Task Added",
                content = "A new task '${task.title}' has been added to ${task.departmentName ?: "General"}.",
                type = "ADD"
            )
        )
    }

    fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        announcementDao.insert(
            Announcement(
                title = "Task Updated",
                content = "The task '${task.title}' in ${task.departmentName ?: "General"} has been updated.",
                type = "UPDATE"
            )
        )
    }

    fun delete(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        announcementDao.insert(
            Announcement(
                title = "Task Deleted",
                content = "The task '${task.title}' from ${task.departmentName ?: "General"} has been deleted.",
                type = "DELETE"
            )
        )
    }
}
