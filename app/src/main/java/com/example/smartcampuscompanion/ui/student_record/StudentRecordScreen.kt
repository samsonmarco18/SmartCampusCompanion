package com.example.smartcampuscompanion.ui.student_record

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.DepartmentWithStudents
import com.example.smartcampuscompanion.data.Student
import com.example.smartcampuscompanion.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRecordScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val studentRecordViewModel: StudentRecordViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val departmentsWithStudents by studentRecordViewModel.departmentsWithStudents.collectAsState()

    val primaryBlue = Color(0xFF004D84)
    val secondaryBlue = Color(0xFF003D69)

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Student Records",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryBlue
                    )
                )
                // Filter bar style from the image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(secondaryBlue)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Department Summary",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    VerticalDivider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.height(16.dp),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "Total: ${departmentsWithStudents.sumOf { it.students.size }}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        containerColor = Color(0xFFF2F2F2)
    ) { paddingValues ->
        if (departmentsWithStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No student records available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                departmentsWithStudents.forEach { departmentWithStudents ->
                    item {
                        DepartmentHeader(departmentWithStudents.department.name)
                    }
                    items(departmentWithStudents.students) { studentWithGrades ->
                        StudentRecordCard(student = studentWithGrades.student)
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentHeader(name: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color(0xFF004D84).copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 16.dp)
                    .background(Color(0xFF004D84), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D84),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StudentRecordCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color(0xFFEEEEEE)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Row {
                        Text(
                            text = "ID: ${student.studentNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${student.yearLevel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Status indicator section (Green for Regular, Red otherwise)
            val isRegular = student.status.equals("Regular", ignoreCase = true)
            val statusColor = if (isRegular) Color(0xFF00897B) else Color(0xFFD32F2F)
            val statusLabel = if (isRegular) "R" else "I"

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
                    .background(statusColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusLabel,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
