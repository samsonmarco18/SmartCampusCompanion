package com.example.smartcampuscompanion.ui.student_record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DepartmentWithStudentsFirestore(
    val departmentName: String,
    val students: List<Student>
)

class StudentRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CampusRepository(database.departmentDao())

    private val _groupedStudents = MutableStateFlow<List<DepartmentWithStudentsFirestore>>(emptyList())
    val groupedStudents: StateFlow<List<DepartmentWithStudentsFirestore>> = _groupedStudents

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allStudents = repository.getAllStudents()
                val grouped = allStudents.groupBy { it.departmentName }
                    .map { (deptName, students) ->
                        DepartmentWithStudentsFirestore(deptName, students)
                    }
                _groupedStudents.value = grouped
            } finally {
                _isLoading.value = false
            }
        }
    }
}
