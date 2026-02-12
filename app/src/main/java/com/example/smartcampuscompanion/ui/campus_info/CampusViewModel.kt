package com.example.smartcampuscompanion.ui.campus_info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.DepartmentWithStudents
import com.example.smartcampuscompanion.data.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CampusRepository

    val departmentsWithStudents: StateFlow<List<DepartmentWithStudents>>

    init {
        val departmentDao = AppDatabase.getDatabase(application).departmentDao()
        repository = CampusRepository(departmentDao)

        // Check and populate the database when the ViewModel is created.
        viewModelScope.launch {
            repository.checkAndPopulate()
        }

        departmentsWithStudents = repository.getDepartmentsWithStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.addStudent(student)
        }
    }

    fun dropStudent(student: Student) {
        viewModelScope.launch {
            repository.dropStudent(student)
        }
    }
}
