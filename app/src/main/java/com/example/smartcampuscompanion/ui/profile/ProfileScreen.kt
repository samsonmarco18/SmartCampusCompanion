package com.example.smartcampuscompanion.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.data.User
import com.example.smartcampuscompanion.repository.UserRepository
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(UserRepository(context), SessionManager(context)))
    val uiState by profileViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Success -> {
            ProfileContent(user = state.user, onUpdate = profileViewModel::updateUser)
        }
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}

@Composable
fun ProfileContent(user: User, onUpdate: (String, String, String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(user.username) }
    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isEditing) {
                EditProfileView(
                    username = newUsername,
                    onUsernameChange = { newUsername = it },
                    password = newPassword,
                    onPasswordChange = { newPassword = it },
                    onSave = { onUpdate(user.username, newUsername, newPassword); isEditing = false },
                    onCancel = { isEditing = false }
                )
            } else {
                UserProfileView(user = user)
            }
        }
    }
}

@Composable
fun UserProfileView(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.AccountCircle, contentDescription = "User Avatar", modifier = Modifier.size(128.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.username, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoRow(icon = Icons.Default.Person, label = "Username", value = user.username)
        // This is static for now, but can be made dynamic in the future by fetching from a remote source
        ProfileInfoRow(icon = Icons.Default.School, label = "Department", value = "Computer Science")
        // This is static for now, but can be made dynamic in the future by fetching from a remote source
        ProfileInfoRow(icon = Icons.Default.School, label = "Year Level", value = "3rd Year")
    }
}

@Composable
fun EditProfileView(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = username, onValueChange = onUsernameChange, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = onPasswordChange, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Button(onClick = onSave) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}


@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text(text = value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
