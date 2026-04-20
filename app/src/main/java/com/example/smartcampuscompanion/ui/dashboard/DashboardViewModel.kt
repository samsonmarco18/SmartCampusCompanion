package com.example.smartcampuscompanion.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Department
import com.example.smartcampuscompanion.data.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    private val _totalDepartments = MutableStateFlow(0)
    val totalDepartments: StateFlow<Int> = _totalDepartments

    private val _totalStudents = MutableStateFlow(0)
    val totalStudents: StateFlow<Int> = _totalStudents

    init {
        fetchStats()
    }

    private fun fetchStats() {
        viewModelScope.launch {
            try {
                val depts = firestore.collection("departments").get().await()
                _totalDepartments.value = depts.size()

                val students = firestore.collection("users")
                    .whereEqualTo("role", "student")
                    .get()
                    .await()
                _totalStudents.value = students.size()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
