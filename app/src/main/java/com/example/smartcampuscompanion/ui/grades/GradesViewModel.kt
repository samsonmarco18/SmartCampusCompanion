package com.example.smartcampuscompanion.ui.grades

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.DepartmentWithStudents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GradesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CampusRepository
    private val _departments = MutableStateFlow<List<DepartmentWithStudents>>(emptyList())
    val departments: StateFlow<List<DepartmentWithStudents>> = _departments.asStateFlow()

    init {
        val departmentDao = AppDatabase.getDatabase(application).departmentDao()
        repository = CampusRepository(departmentDao)
        repository.getDepartmentsWithStudents()
            .onEach { _departments.value = it }
            .launchIn(viewModelScope)
    }
}
