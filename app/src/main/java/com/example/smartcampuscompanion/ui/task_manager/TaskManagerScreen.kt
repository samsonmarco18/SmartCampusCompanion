package com.example.smartcampuscompanion.ui.task_manager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
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
import com.example.smartcampuscompanion.ui.theme.BeigeBackground
import com.example.smartcampuscompanion.ui.theme.BeigePrimary
import com.example.smartcampuscompanion.ui.theme.BeigeSecondary
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
                title = { Text("To-Do List", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BeigePrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showDialog = true
                },
                containerColor = BeigePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = BeigeBackground
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (tasks.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
            Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(80.dp), tint = BeigePrimary.copy(alpha = 0.4f))
            Text(
                text = "No Tasks Yet",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = BeigeSecondary
            )
            Text(
                text = "Tap the '+' button to add a new task and get organized.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(BeigePrimary))
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BeigeSecondary)
                    Text(text = task.description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = "Due date", modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(task.dueDate)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
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

    val initialDateTime = remember(task) { Calendar.getInstance().apply { if (task != null) timeInMillis = task.dueDate else timeInMillis = System.currentTimeMillis() } }
    var pickedDate by remember { mutableStateOf(initialDateTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()) }
    var pickedTime by remember { mutableStateOf(initialDateTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = if (task == null) "Add New Task" else "Edit Task", style = MaterialTheme.typography.headlineMedium, color = BeigeSecondary)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Task Description") }, modifier = Modifier.fillMaxWidth())

                val finalDateTime = remember(pickedDate, pickedTime) { Calendar.getInstance().apply { set(pickedDate.year, pickedDate.monthValue - 1, pickedDate.dayOfMonth, pickedTime.hour, pickedTime.minute) } }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TextButton(onClick = { dateDialogState.show() }) { Text(SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(finalDateTime.time)) }
                    TextButton(onClick = { timeDialogState.show() }) { Text(SimpleDateFormat("h:mm a", Locale.getDefault()).format(finalDateTime.time)) }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                        val taskToSave = task?.copy(
                            title = title, description = description, dueDate = finalDateTime.timeInMillis
                        ) ?: Task(title = title, description = description, dueDate = finalDateTime.timeInMillis)
                        onSave(taskToSave)
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = BeigePrimary)
                    ) { Text("Save") }
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
