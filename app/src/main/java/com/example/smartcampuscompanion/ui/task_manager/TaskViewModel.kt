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
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
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
    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
                                    val id = localTask?.id ?: 0
                                    val updatedTask = firestoreTask.copy(id = id)
                                    taskDao.insert(updatedTask)
                                    scheduleTaskNotifications(updatedTask)
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
                    val updatedTask = task.copy(docId = docRef.id, isSynced = true)
                    taskDao.update(updatedTask)
                    scheduleTaskNotifications(updatedTask)
                } else {
                    // Existing task modified offline
                    firestore.collection("tasks").document(task.docId)
                        .set(task.copy(isSynced = true)).await()
                    val updatedTask = task.copy(isSynced = true)
                    taskDao.update(updatedTask)
                    scheduleTaskNotifications(updatedTask)
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
        val rowId: Long = taskDao.insert(newTask)
        val taskWithId = newTask.copy(id = rowId.toInt())
        scheduleTaskNotifications(taskWithId)
        
        // 2. Try to sync with Firestore
        try {
            val docRef = firestore.collection("tasks").add(newTask.copy(isSynced = true)).await()
            taskDao.update(taskWithId.copy(docId = docRef.id, isSynced = true))
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
        scheduleTaskNotifications(updatedTask)
        
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
        cancelTaskNotifications(task)
        
        // 2. Delete from Firestore
        if (task.docId.isNotEmpty()) {
            try {
                firestore.collection("tasks").document(task.docId).delete().await()
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Delete sync failed", e)
            }
        }
    }

    private fun scheduleTaskNotifications(task: Task) {
        val currentTime = System.currentTimeMillis()
        
        // Notify at start
        if (task.startDate > currentTime) {
            scheduleAlarm(task, task.startDate, "Task Started!", "Your task '${task.title}' has started.")
        }

        // Notify at deadline
        if (task.dueDate > currentTime) {
            scheduleAlarm(task, task.dueDate, "Task Due Now!", "Your task '${task.title}' is due now.")
        }
        
        // Notify 30 minutes before
        val thirtyMinBefore = task.dueDate - (30 * 60 * 1000)
        if (thirtyMinBefore > currentTime) {
            scheduleAlarm(task, thirtyMinBefore, "Task Reminder", "Your task '${task.title}' is due in 30 minutes.")
        }
        
        // Notify 1 hour before
        val oneHourBefore = task.dueDate - (60 * 60 * 1000)
        if (oneHourBefore > currentTime) {
            scheduleAlarm(task, oneHourBefore, "Task Reminder", "Your task '${task.title}' is due in 1 hour.")
        }
    }

    private fun scheduleAlarm(task: Task, time: Long, title: String, message: String) {
        val intent = Intent(getApplication(), TaskAlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("taskId", task.id)
        }
        
        val requestCode = (task.id.toString() + time.toString()).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    private fun cancelTaskNotifications(task: Task) {
        val intent = Intent(getApplication(), TaskAlarmReceiver::class.java)
        
        // Cancel all possible alarms for this task
        val times = listOf(
            task.startDate,
            task.dueDate, 
            task.dueDate - (30 * 60 * 1000), 
            task.dueDate - (60 * 60 * 1000)
        )
        times.forEach { time ->
            val requestCode = (task.id.toString() + time.toString()).hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
