package com.example.smartcampuscompanion.ui.task_manager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.Task
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    taskViewModel: TaskViewModel = viewModel(),
    onNavigateUp: () -> Unit
) {
    val tasks by taskViewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (tasks.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onDelete = { taskViewModel.delete(task) },
                            onEdit = { taskToEdit = task; showDialog = true }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            TaskDialog(
                task = taskToEdit,
                onDismiss = { showDialog = false },
                onSave = { taskToSave ->
                    if (taskToEdit == null) taskViewModel.insert(taskToSave) else taskViewModel.update(taskToSave)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                Icons.Default.TaskAlt, 
                contentDescription = null, 
                modifier = Modifier.size(80.dp), 
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Text(
                text = "No Tasks Yet",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tap the '+' button to add a new task and get organized.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = task.title, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = task.description, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Start Date display
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow, 
                            contentDescription = "Start time", 
                            modifier = Modifier.size(16.dp), 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start: " + SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(task.startDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Due Date display
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule, 
                            contentDescription = "Due date", 
                            modifier = Modifier.size(16.dp), 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Due:   " + SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(task.dueDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete Task", 
                        tint = MaterialTheme.colorScheme.error
                    )
                }
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
    var description by remember(task) { mutableStateOf(task?.description ?: "") }

    val initialStartDateTime = remember(task) { 
        Calendar.getInstance().apply { 
            if (task != null && task.startDate != 0L) timeInMillis = task.startDate else timeInMillis = System.currentTimeMillis() 
        } 
    }
    var pickedStartDate by remember { mutableStateOf(initialStartDateTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate()) }
    var pickedStartTime by remember { mutableStateOf(initialStartDateTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalTime()) }

    val initialDueDateTime = remember(task) { 
        Calendar.getInstance().apply { 
            if (task != null) timeInMillis = task.dueDate else timeInMillis = System.currentTimeMillis() + 3600000 // default to 1 hour later
        } 
    }
    var pickedDueDate by remember { mutableStateOf(initialDueDateTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate()) }
    var pickedDueTime by remember { mutableStateOf(initialDueDateTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalTime()) }

    val startDateDialogState = rememberMaterialDialogState()
    val startTimeDialogState = rememberMaterialDialogState()
    val dueDateDialogState = rememberMaterialDialogState()
    val dueTimeDialogState = rememberMaterialDialogState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp), 
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState()), 
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (task == null) "Add New Task" else "Edit Task", 
                    style = MaterialTheme.typography.headlineSmall, 
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Task Title") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = { Text("Task Description") }, 
                    modifier = Modifier.fillMaxWidth()
                )

                val finalStartDateTime = remember(pickedStartDate, pickedStartTime) { 
                    Calendar.getInstance().apply { set(pickedStartDate.year, pickedStartDate.monthValue - 1, pickedStartDate.dayOfMonth, pickedStartTime.hour, pickedStartTime.minute) } 
                }
                val finalDueDateTime = remember(pickedDueDate, pickedDueTime) { 
                    Calendar.getInstance().apply { set(pickedDueDate.year, pickedDueDate.monthValue - 1, pickedDueDate.dayOfMonth, pickedDueTime.hour, pickedDueTime.minute) } 
                }

                Text("Start Schedule:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TextButton(onClick = { startDateDialogState.show() }) { 
                        Text(
                            text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(finalStartDateTime.time),
                            color = MaterialTheme.colorScheme.primary
                        ) 
                    }
                    TextButton(onClick = { startTimeDialogState.show() }) { 
                        Text(
                            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(finalStartDateTime.time),
                            color = MaterialTheme.colorScheme.primary
                        ) 
                    }
                }

                Text("Deadline Schedule:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TextButton(onClick = { dueDateDialogState.show() }) { 
                        Text(
                            text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(finalDueDateTime.time),
                            color = MaterialTheme.colorScheme.primary
                        ) 
                    }
                    TextButton(onClick = { dueTimeDialogState.show() }) { 
                        Text(
                            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(finalDueDateTime.time),
                            color = MaterialTheme.colorScheme.primary
                        ) 
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { 
                        Text("Cancel", color = MaterialTheme.colorScheme.secondary) 
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val taskToSave = task?.copy(
                                title = title, 
                                description = description, 
                                startDate = finalStartDateTime.timeInMillis,
                                dueDate = finalDueDateTime.timeInMillis
                            ) ?: Task(
                                title = title, 
                                description = description, 
                                startDate = finalStartDateTime.timeInMillis,
                                dueDate = finalDueDateTime.timeInMillis,
                                studentNumber = "" // Set in ViewModel
                            )
                            onSave(taskToSave)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { 
                        Text("Save") 
                    }
                }
            }
        }
    }

    // Dialogs for Start Date and Time
    MaterialDialog(
        dialogState = startDateDialogState, 
        buttons = { positiveButton("Ok"); negativeButton("Cancel") },
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        datepicker(initialDate = pickedStartDate, title = "Pick start date") { date -> pickedStartDate = date }
    }

    MaterialDialog(
        dialogState = startTimeDialogState, 
        buttons = { positiveButton("Ok"); negativeButton("Cancel") },
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        timepicker(initialTime = pickedStartTime, title = "Pick start time") { time -> pickedStartTime = time }
    }

    // Dialogs for Due Date and Time
    MaterialDialog(
        dialogState = dueDateDialogState, 
        buttons = { positiveButton("Ok"); negativeButton("Cancel") },
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        datepicker(initialDate = pickedDueDate, title = "Pick due date") { date -> pickedDueDate = date }
    }

    MaterialDialog(
        dialogState = dueTimeDialogState, 
        buttons = { positiveButton("Ok"); negativeButton("Cancel") },
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        timepicker(initialTime = pickedDueTime, title = "Pick due time") { time -> pickedDueTime = time }
    }
}
