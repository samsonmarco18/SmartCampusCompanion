package com.example.smartcampuscompanion.ui.campus_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Student(val name: String, val email: String, val yearLevel: String)
data class Department(val name: String, val contact: String, val students: List<Student>)

val departments = listOf(
    Department(
        name = "Computer Science",
        contact = "cs.office@university.edu",
        students = listOf(
            Student("Alice", "alice@student.university.edu", "4th Year"),
            Student("Bob", "bob@student.university.edu", "3rd Year"),
            Student("Charlie", "charlie@student.university.edu", "2nd Year")
        )
    ),
    Department(
        name = "Electrical Engineering",
        contact = "ee.office@university.edu",
        students = listOf(
            Student("David", "david@student.university.edu", "4th Year"),
            Student("Eve", "eve@student.university.edu", "3rd Year"),
            Student("Frank", "frank@student.university.edu", "1st Year")
        )
    ),
    Department(
        name = "Mechanical Engineering",
        contact = "me.office@university.edu",
        students = listOf(
            Student("Grace", "grace@student.university.edu", "2nd Year"),
            Student("Heidi", "heidi@student.university.edu", "4th Year"),
            Student("Ivan", "ivan@student.university.edu", "3rd Year")
        )
    ),
    Department(
        name = "Civil Engineering",
        contact = "ce.office@university.edu",
        students = listOf(
            Student("Judy", "judy@student.university.edu", "1st Year"),
            Student("Mallory", "mallory@student.university.edu", "2nd Year"),
            Student("Trent", "trent@student.university.edu", "4th Year")
        )
    ),
    Department(
        name = "Biology",
        contact = "bio.office@university.edu",
        students = listOf(
            Student("Walter", "walter@student.university.edu", "3rd Year"),
            Student("Peggy", "peggy@student.university.edu", "2nd Year"),
            Student("Victor", "victor@student.university.edu", "1st Year")
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(onNavigateUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Departments") },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(0.dp)) } // For spacing at the top
            items(departments) { department ->
                DepartmentCard(department)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // For spacing at the bottom
        }
    }
}

@Composable
fun DepartmentCard(department: Department) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(department.name, style = MaterialTheme.typography.titleLarge)
            Text(department.contact, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Students", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                department.students.forEach { student ->
                    StudentRow(student)
                }
            }
        }
    }
}

@Composable
fun StudentRow(student: Student) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Student Icon",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(student.name, style = MaterialTheme.typography.bodyLarge)
            Text(student.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(student.yearLevel, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
