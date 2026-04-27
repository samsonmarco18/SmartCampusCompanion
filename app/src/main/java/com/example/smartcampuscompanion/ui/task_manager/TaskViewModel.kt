package com.example.smartcampuscompanion.ui.task_manager

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.Task
import com.example.smartcampuscompanion.util.NotificationReceiver
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
                                if (localTask == null) {
                                    // Robust check for duplicates by comparing content
                                    // This prevents the case where a task was created offline, synced, but is then 
                                    // received back from Firestore before the local DB is updated with the docId.
                                    val currentLocalTasks = tasks.first() 
                                    val existingDuplicate = currentLocalTasks.find { 
                                        it.title == firestoreTask.title && 
                                        it.dueDate == firestoreTask.dueDate &&
                                        it.startDate == firestoreTask.startDate &&
                                        it.docId.isEmpty() // Only match tasks not yet linked
                                    }
                                    
                                    if (existingDuplicate != null) {
                                        taskDao.update(existingDuplicate.copy(docId = firestoreTask.docId, isSynced = true))
                                    } else {
                                        taskDao.insert(firestoreTask)
                                        scheduleTaskNotifications(firestoreTask)
                                    }
                                } else if (firestoreTask.lastModified > localTask.lastModified) {
                                    taskDao.update(firestoreTask.copy(id = localTask.id))
                                    scheduleTaskNotifications(firestoreTask.copy(id = localTask.id))
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
                    // Check if already in Firestore to avoid duplicate creation
                    val existing = firestore.collection("tasks")
                        .whereEqualTo("studentNumber", studentNumber)
                        .whereEqualTo("title", task.title)
                        .whereEqualTo("dueDate", task.dueDate)
                        .get().await()
                    
                    if (existing.isEmpty) {
                        val docRef = firestore.collection("tasks").add(task.copy(isSynced = true)).await()
                        taskDao.update(task.copy(docId = docRef.id, isSynced = true))
                    } else {
                        val remoteDocId = existing.documents.first().id
                        taskDao.update(task.copy(docId = remoteDocId, isSynced = true))
                    }
                } else {
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
        val rowId = taskDao.insert(newTask)
        val insertedTask = newTask.copy(id = rowId.toInt())
        scheduleTaskNotifications(insertedTask)
        
        try {
            // Check for potential duplicate in Firestore before adding
            val existing = firestore.collection("tasks")
                .whereEqualTo("studentNumber", studentNumber)
                .whereEqualTo("title", newTask.title)
                .whereEqualTo("dueDate", newTask.dueDate)
                .get().await()

            if (existing.isEmpty) {
                val docRef = firestore.collection("tasks").add(insertedTask.copy(isSynced = true)).await()
                taskDao.update(insertedTask.copy(docId = docRef.id, isSynced = true))
            } else {
                val remoteDocId = existing.documents.first().id
                taskDao.update(insertedTask.copy(docId = remoteDocId, isSynced = true))
            }
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Initial sync failed", e)
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        val updatedTask = task.copy(
            isSynced = false,
            lastModified = System.currentTimeMillis()
        )
        taskDao.update(updatedTask)
        scheduleTaskNotifications(updatedTask)
        
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
        cancelTaskNotifications(task)
        taskDao.delete(task)
        if (task.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(task.docId).delete().await()
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Delete sync failed", e)
            }
        }
    }

    private fun scheduleTaskNotifications(task: Task) {
        if (task.id == 0) return 
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (task.startDate > System.currentTimeMillis()) {
            val intent = Intent(getApplication(), NotificationReceiver::class.java).apply {
                putExtra("taskId", task.id * 2)
                putExtra("title", "Task Starting Now")
                putExtra("message", "It's time to start: ${task.title}")
            }
            val pendingIntent = PendingIntent.getBroadcast(
                getApplication(), task.id * 2, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.startDate, pendingIntent)
        }

        if (task.dueDate > System.currentTimeMillis()) {
            val intent = Intent(getApplication(), NotificationReceiver::class.java).apply {
                putExtra("taskId", task.id * 2 + 1)
                putExtra("title", "Task Deadline Near")
                putExtra("message", "Deadline for: ${task.title}")
            }
            val pendingIntent = PendingIntent.getBroadcast(
                getApplication(), task.id * 2 + 1, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.dueDate, pendingIntent)
        }
    }

    private fun cancelTaskNotifications(task: Task) {
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(getApplication(), NotificationReceiver::class.java)
        
        val p1 = PendingIntent.getBroadcast(getApplication(), task.id * 2, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        p1?.let { alarmManager.cancel(it) }

        val p2 = PendingIntent.getBroadcast(getApplication(), task.id * 2 + 1, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        p2?.let { alarmManager.cancel(it) }
    }
}
