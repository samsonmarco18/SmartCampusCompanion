package com.example.smartcampuscompanion.ui.task_manager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.Task
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(viewModel: TaskViewModel = viewModel(), onNavigateUp: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                taskToEdit = null // Clear task to signal creation
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onDelete = { viewModel.delete(task) },
                    onEdit = {
                        taskToEdit = task // Set task to be edited
                        showDialog = true
                    }
                )
            }
        }

        if (showDialog) {
            TaskDialog(
                task = taskToEdit,
                onDismiss = { showDialog = false },
                onSave = { taskToSave ->
                    if (taskToEdit == null) {
                        viewModel.insert(taskToSave)
                    } else {
                        viewModel.update(taskToSave)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Due: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(task.dueDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }

    val initialDateTime = Calendar.getInstance().apply {
        if (task != null) timeInMillis = task.dueDate else timeInMillis = System.currentTimeMillis()
    }
    var pickedDate by remember { mutableStateOf(initialDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) }
    var pickedTime by remember { mutableStateOf(initialDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = if (task == null) "Add Task" else "Edit Task", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { dateDialogState.show() }) { Text("Select Date") }
                    Button(onClick = { timeDialogState.show() }) { Text("Select Time") }
                }
                Spacer(modifier = Modifier.height(8.dp))

                val finalDateTime = Calendar.getInstance().apply {
                    set(pickedDate.year, pickedDate.monthValue - 1, pickedDate.dayOfMonth, pickedTime.hour, pickedTime.minute)
                }
                Text(
                    text = "Due: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(finalDateTime.time)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val taskToSave = task?.copy(
                            title = title,
                            description = description,
                            dueDate = finalDateTime.timeInMillis
                        ) ?: Task(
                            title = title,
                            description = description,
                            dueDate = finalDateTime.timeInMillis
                        )
                        onSave(taskToSave)
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }

    MaterialDialog(dialogState = dateDialogState, buttons = { positiveButton("Ok"); negativeButton("Cancel") }) {
        datepicker(initialDate = pickedDate, title = "Pick a date") { date -> pickedDate = date }
    }

    MaterialDialog(dialogState = timeDialogState, buttons = { positiveButton("Ok"); negativeButton("Cancel") }) {
        timepicker(initialTime = pickedTime, title = "Pick a time") { time -> pickedTime = time }
    }
}
