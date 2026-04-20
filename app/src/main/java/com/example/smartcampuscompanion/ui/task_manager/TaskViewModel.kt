package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val sessionManager = SessionManager(application)
    private val studentNumber = sessionManager.fetchStudentNumber() ?: ""

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        if (studentNumber.isBlank()) return

        firestore.collection("tasks")
            .whereEqualTo("studentNumber", studentNumber)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _tasks.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Task::class.java)?.apply {
                            docId = doc.id
                        }
                    }
                }
            }
    }

    fun insert(task: Task) = viewModelScope.launch {
        try {
            firestore.collection("tasks").add(task.copy(studentNumber = studentNumber)).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        if (task.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(task.docId)
                    .set(task.copy(studentNumber = studentNumber)).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        if (task.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(task.docId).delete().await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
