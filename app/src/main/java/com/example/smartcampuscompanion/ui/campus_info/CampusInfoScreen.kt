package com.example.smartcampuscompanion.ui.campus_info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Department(val name: String, val contact: String)

val departments = listOf(
    Department("Computer Science", "cs@example.com"),
    Department("Electrical Engineering", "ee@example.com"),
    Department("Mechanical Engineering", "me@example.com")
)

@Composable
fun CampusInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Departments")
        LazyColumn {
            items(departments) { department ->
                Text("${department.name}: ${department.contact}")
            }
        }
    }
}
