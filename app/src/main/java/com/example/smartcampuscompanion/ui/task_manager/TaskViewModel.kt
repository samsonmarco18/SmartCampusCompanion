package com.example.smartcampuscompanion.ui.task_manager

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val sessionManager = SessionManager(application)
    private val studentNumber = sessionManager.fetchStudentNumber() ?: ""
    private val taskDao = AppDatabase.getDatabase(application).taskDao()

    val tasks: StateFlow<List<Task>> = taskDao.getTasksForStudent(studentNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeFirestoreTasks()
        syncOfflineTasks()
    }

    private fun observeFirestoreTasks() {
        if (studentNumber.isBlank()) return

        firestore.collection("tasks")
            .whereEqualTo("studentNumber", studentNumber)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("TaskViewModel", "Firestore listener failed", e)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    viewModelScope.launch {
                        snapshot.documents.forEach { doc ->
                            val firestoreTask = doc.toObject(Task::class.java)?.apply {
                                docId = doc.id
                                isSynced = true
                            }
                            if (firestoreTask != null) {
                                val localTask = taskDao.getTaskByDocId(firestoreTask.docId)
                                if (localTask == null || firestoreTask.lastModified > localTask.lastModified) {
                                    taskDao.insert(firestoreTask.copy(id = localTask?.id ?: 0))
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun syncOfflineTasks() = viewModelScope.launch {
        if (studentNumber.isBlank()) return@launch
        
        val unsyncedTasks = taskDao.getUnsyncedTasks(studentNumber)
        unsyncedTasks.forEach { task ->
            try {
                if (task.docId.isEmpty()) {
                    // New task created offline
                    val docRef = firestore.collection("tasks").add(task.copy(isSynced = true)).await()
                    taskDao.update(task.copy(docId = docRef.id, isSynced = true))
                } else {
                    // Existing task modified offline
                    firestore.collection("tasks").document(task.docId)
                        .set(task.copy(isSynced = true)).await()
                    taskDao.update(task.copy(isSynced = true))
                }
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Sync failed for task ${task.id}", e)
            }
        }
    }

    fun insert(task: Task) = viewModelScope.launch {
        val newTask = task.copy(
            studentNumber = studentNumber, 
            isSynced = false,
            lastModified = System.currentTimeMillis()
        )
        // 1. Save locally immediately
        taskDao.insert(newTask)
        
        // 2. Try to sync with Firestore
        try {
            val docRef = firestore.collection("tasks").add(newTask.copy(isSynced = true)).await()
            taskDao.update(newTask.copy(docId = docRef.id, isSynced = true))
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Initial sync failed, will retry later", e)
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        val updatedTask = task.copy(
            isSynced = false,
            lastModified = System.currentTimeMillis()
        )
        // 1. Update locally immediately
        taskDao.update(updatedTask)
        
        // 2. Try to sync
        if (updatedTask.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(updatedTask.docId)
                    .set(updatedTask.copy(isSynced = true)).await()
                taskDao.update(updatedTask.copy(isSynced = true))
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Update sync failed", e)
            }
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        // 1. Delete locally
        taskDao.delete(task)
        
        // 2. Delete from Firestore
        if (task.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(task.docId).delete().await()
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Delete sync failed", e)
            }
        }
    }
}
