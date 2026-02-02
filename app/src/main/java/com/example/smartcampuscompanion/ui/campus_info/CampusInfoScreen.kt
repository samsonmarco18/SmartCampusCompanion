package com.example.smartcampuscompanion.ui.campus_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
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
    ),
    Department(
        name = "Information Technology",
        contact = "it.office@university.edu",
        students = listOf(
            Student("Xavier", "xavier@student.university.edu", "1st Year"),
            Student("Yara", "yara@student.university.edu", "2nd Year"),
            Student("Zane", "zane@student.university.edu", "3rd Year")
        )
    ),
    Department(
        name = "Chemical Engineering",
        contact = "che.office@university.edu",
        students = listOf(
            Student("Aaron", "aaron@student.university.edu", "4th Year"),
            Student("Brian", "brian@student.university.edu", "2nd Year"),
            Student("Chloe", "chloe@student.university.edu", "1st Year")
        )
    ),
    Department(
        name = "Physics",
        contact = "phy.office@university.edu",
        students = listOf(
            Student("Diana", "diana@student.university.edu", "3rd Year"),
            Student("Ethan", "ethan@student.university.edu", "4th Year"),
            Student("Fiona", "fiona@student.university.edu", "2nd Year")
        )
    )
)

val departmentColors = listOf(
    Color(0xFFFADBD8),
    Color(0xFFEBDEF0),
    Color(0xFFD6EAF8),
    Color(0xFFD1F2EB),
    Color(0xFFFCF3CF),
    Color(0xFFFDEBD0),
    Color(0xFFE5E7E9),
    Color(0xFFE8DAEF)
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
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(departments) { index, department ->
                DepartmentCard(
                    department = department,
                    containerColor = departmentColors[index % departmentColors.size]
                )
            }
        }
    }
}

@Composable
fun DepartmentCard(department: Department, containerColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = department.contact,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Students", style = MaterialTheme.typography.titleLarge)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    department.students.forEach { student ->
                        StudentRow(student)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentRow(student: Student) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null, // Decorative
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = student.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = student.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = student.yearLevel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
