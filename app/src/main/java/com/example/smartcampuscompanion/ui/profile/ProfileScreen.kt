package com.example.smartcampuscompanion.ui.profile

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smartcampuscompanion.data.Student
import com.example.smartcampuscompanion.ui.theme.BeigeBackground
import com.example.smartcampuscompanion.ui.theme.BeigePrimary
import com.example.smartcampuscompanion.ui.theme.BeigeSecondary
import com.example.smartcampuscompanion.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateUp: () -> Unit = {}) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
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
        containerColor = BeigeBackground
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BeigePrimary)
                    }
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        student = state.student,
                        onUpdate = profileViewModel::updateUser,
                        onUpdateImage = profileViewModel::updateUserProfileImage
                    )
                }
                is ProfileState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    student: Student,
    onUpdate: (String, String, String) -> Unit,
    onUpdateImage: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(student.name) }
    var newPassword by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onUpdateImage(it.toString()) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isEditing) {
                EditProfileView(
                    username = newUsername,
                    onUsernameChange = { newUsername = it },
                    password = newPassword,
                    onPasswordChange = { newPassword = it },
                    onSave = {
                        onUpdate(student.name, newUsername, newPassword)
                        isEditing = false
                    },
                    onCancel = { isEditing = false }
                )
            } else {
                UserProfileView(
                    student = student,
                    onImageClick = { launcher.launch("image/*") }
                )
            }
        }

        if (!isEditing) {
            FloatingActionButton(
                onClick = { isEditing = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = BeigePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    }
}

@Composable
fun UserProfileView(student: Student, onImageClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .size(120.dp)
                .clickable { onImageClick() },
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            if (student.profileImageUrl != null) {
                AsyncImage(
                    model = student.profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize(),
                    tint = BeigePrimary.copy(alpha = 0.6f)
                )
            }
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    color = BeigePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp).padding(4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = student.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BeigeSecondary
        )
        Text(
            text = "Student Account",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SectionHeader("Personal Information")
        ProfileInfoRow(icon = Icons.Default.Person, label = "Username", value = student.name)
        ProfileInfoRow(icon = Icons.Default.School, label = "Department", value = student.departmentName)
        ProfileInfoRow(icon = Icons.Default.School, label = "Year Level", value = student.yearLevel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionHeader(name: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = BeigePrimary.copy(alpha = 0.1f),
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
                    .background(BeigePrimary, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BeigePrimary,
                letterSpacing = 0.5.sp
            )
        }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Edit Profile", style = MaterialTheme.typography.headlineSmall, color = BeigeSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = BeigePrimary)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = BeigeBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = BeigePrimary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.titleMedium, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}
