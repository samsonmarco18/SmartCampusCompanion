package com.example.smartcampuscompanion.ui.grades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(viewModel: GradesViewModel = viewModel(), onNavigateUp: () -> Unit) {
    val departments by viewModel.departments.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grades") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(departments) { departmentWithStudents ->
                Text(
                    departmentWithStudents.department.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                departmentWithStudents.students.forEach { studentWithGrades ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(studentWithGrades.student.name, fontWeight = FontWeight.Bold)
                            studentWithGrades.grades.forEach { grade ->
                                Row {
                                    Text(text = grade.subject)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = grade.grade.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
