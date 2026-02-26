package com.example.smartcampuscompanion.ui.announcement_manager

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.ui.campus_info.CampusViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementManagerScreen(
    announcementManagerViewModel: AnnouncementManagerViewModel = viewModel(),
    campusViewModel: CampusViewModel = viewModel(),
    onNavigateUp: () -> Unit
) {
    val announcements by announcementManagerViewModel.announcements.collectAsState()
    val departments by campusViewModel.departmentsWithStudents.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var announcementToEdit by remember { mutableStateOf<Announcement?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcement Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                announcementToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Announcement")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (announcements.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(announcements, key = { it.id }) { announcement ->
                        AnnouncementItem(
                            announcement = announcement,
                            onDelete = { announcementManagerViewModel.delete(announcement) },
                            onEdit = { announcementToEdit = announcement; showDialog = true }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AnnouncementDialog(
                announcement = announcementToEdit,
                departments = departments.map { it.department },
                onDismiss = { showDialog = false },
                onSave = { announcementToSave ->
                    if (announcementToEdit == null) announcementManagerViewModel.insert(announcementToSave) else announcementManagerViewModel.update(announcementToSave)
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
            Icon(Icons.Default.Announcement, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
            Text(
                text = "No Announcements Yet",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tap the '+' button to add a new announcement.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary)) {}
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = announcement.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    ChipView(text = announcement.category)
                    if (announcement.departmentName != null) {
                        ChipView(text = announcement.departmentName)
                    }
                    Text(text = announcement.content, style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = "Due date", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(announcement.dueDate)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Announcement", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ChipView(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnouncementDialog(
    announcement: Announcement?,
    departments: List<com.example.smartcampuscompanion.data.Department>,
    onDismiss: () -> Unit,
    onSave: (Announcement) -> Unit
) {
    var title by remember { mutableStateOf(announcement?.title ?: "") }
    var content by remember(announcement) { mutableStateOf(announcement?.content ?: "") }
    var expandedDepartment by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf(announcement?.departmentName) }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(announcement?.category) }
    val categories = listOf("Event", "Activity", "Urgent", "Seminar")

    val initialDateTime = remember(announcement) { Calendar.getInstance().apply { if (announcement != null) timeInMillis = announcement.dueDate else timeInMillis = System.currentTimeMillis() } }
    var pickedDate by remember { mutableStateOf(initialDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) }
    var pickedTime by remember { mutableStateOf(initialDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = if (announcement == null) "Add New Announcement" else "Edit Announcement", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Announcement Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Announcement Content") }, modifier = Modifier.fillMaxWidth())

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expandedDepartment = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text(selectedDepartment ?: "Assign to Department")
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expandedDepartment, onDismissRequest = { expandedDepartment = false }, modifier = Modifier.fillMaxWidth()) {
                        departments.forEach {
                            DropdownMenuItem(text = { Text(it.name) }, onClick = { selectedDepartment = it.name; expandedDepartment = false })
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expandedCategory = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text(selectedCategory ?: "Select Category")
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }, modifier = Modifier.fillMaxWidth()) {
                        categories.forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { selectedCategory = it; expandedCategory = false })
                        }
                    }
                }

                val finalDateTime = remember(pickedDate, pickedTime) { Calendar.getInstance().apply { set(pickedDate.year, pickedDate.monthValue - 1, pickedDate.dayOfMonth, pickedTime.hour, pickedTime.minute) } }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TextButton(onClick = { dateDialogState.show() }) { Text(SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(finalDateTime.time)) }
                    TextButton(onClick = { timeDialogState.show() }) { Text(SimpleDateFormat("h:mm a", Locale.getDefault()).format(finalDateTime.time)) }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (selectedCategory != null) {
                            val announcementToSave = announcement?.copy(
                                title = title, content = content, dueDate = finalDateTime.timeInMillis, departmentName = selectedDepartment, category = selectedCategory!!
                            ) ?: Announcement(title = title, content = content, dueDate = finalDateTime.timeInMillis, departmentName = selectedDepartment, category = selectedCategory!!)
                            onSave(announcementToSave)
                        }
                    }) { Text("Save") }
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
