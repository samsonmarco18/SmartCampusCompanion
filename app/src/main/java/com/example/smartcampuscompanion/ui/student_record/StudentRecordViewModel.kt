package com.example.smartcampuscompanion.ui.student_record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.DepartmentWithStudents
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StudentRecordViewModel(application: Application, campusRepository: CampusRepository) : AndroidViewModel(application) {

    private val repository: CampusRepository

    val departmentsWithStudents: StateFlow<List<DepartmentWithStudents>>

    init {
        val departmentDao = AppDatabase.getDatabase(application).departmentDao()
        repository = CampusRepository(departmentDao)
        departmentsWithStudents = repository.getDepartmentsWithStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}