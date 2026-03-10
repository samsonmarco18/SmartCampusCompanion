package com.example.smartcampuscompanion.ui.signup

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcampuscompanion.ui.theme.BeigeBackground
import com.example.smartcampuscompanion.ui.theme.BeigePrimary
import com.example.smartcampuscompanion.ui.theme.BeigeSecondary
import com.example.smartcampuscompanion.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var studentNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val signUpViewModel: SignUpViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val signUpState by signUpViewModel.signUpState.collectAsState()
    var showStudentNumberExistsDialog by remember { mutableStateOf(false) }
    var showGoToMsidDialog by remember { mutableStateOf(false) }

    val departments = listOf(
        "Computer Science",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Biology",
        "Information Technology",
        "Chemical Engineering",
        "Physics"
    )
    val yearLevels = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

    var selectedDepartment by remember { mutableStateOf<String?>(null) }
    var selectedYearLevel by remember { mutableStateOf<String?>(null) }
    var departmentExpanded by remember { mutableStateOf(false) }
    var yearLevelExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(signUpState) {
        when (val state = signUpState) {
            is SignUpState.Success -> {
                onSignUpSuccess()
            }
            is SignUpState.Error -> {
                if (state.message == "Student number already exists") {
                    showStudentNumberExistsDialog = true
                }
            }
            else -> {}
        }
    }

    if (showStudentNumberExistsDialog) {
        AlertDialog(
            onDismissRequest = { showStudentNumberExistsDialog = false },
            title = { Text("Student number already exists") },
            text = { Text("Is this yours?") },
            confirmButton = {
                Button(
                    onClick = {
                        showStudentNumberExistsDialog = false
                        showGoToMsidDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BeigePrimary)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStudentNumberExistsDialog = false }) {
                    Text("No", color = BeigeSecondary)
                }
            }
        )
    }

    if (showGoToMsidDialog) {
        AlertDialog(
            onDismissRequest = { showGoToMsidDialog = false },
            title = { Text("Validation Required") },
            text = { Text("Go to MSID for validation") },
            confirmButton = {
                Button(
                    onClick = { showGoToMsidDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BeigePrimary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        containerColor = BeigeBackground
    ) {
        paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = BeigePrimary,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BeigeSecondary
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = studentNumber,
                            onValueChange = { studentNumber = it },
                            label = { Text("Student Number") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BeigePrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BeigePrimary,
                                focusedLabelColor = BeigePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BeigePrimary) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BeigePrimary,
                                focusedLabelColor = BeigePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BeigePrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BeigePrimary,
                                focusedLabelColor = BeigePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BeigePrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { signUpViewModel.signUp(studentNumber, username, password, confirmPassword, selectedDepartment, selectedYearLevel) }),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BeigePrimary,
                                focusedLabelColor = BeigePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = departmentExpanded,
                            onExpandedChange = { departmentExpanded = !departmentExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedDepartment ?: "",
                                onValueChange = {},
                                label = { Text("Department") },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BeigePrimary,
                                    focusedLabelColor = BeigePrimary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = departmentExpanded,
                                onDismissRequest = { departmentExpanded = false },
                            ) {
                                departments.forEach { department ->
                                    DropdownMenuItem(
                                        text = { Text(department) },
                                        onClick = {
                                            selectedDepartment = department
                                            selectedYearLevel = null
                                            departmentExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (selectedDepartment != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ExposedDropdownMenuBox(
                                expanded = yearLevelExpanded,
                                onExpandedChange = { yearLevelExpanded = !yearLevelExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedYearLevel ?: "",
                                    onValueChange = {},
                                    label = { Text("Year Level") },
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearLevelExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BeigePrimary,
                                        focusedLabelColor = BeigePrimary
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = yearLevelExpanded,
                                    onDismissRequest = { yearLevelExpanded = false },
                                ) {
                                    yearLevels.forEach { yearLevel ->
                                        DropdownMenuItem(
                                            text = { Text(yearLevel) },
                                            onClick = {
                                                selectedYearLevel = yearLevel
                                                yearLevelExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        AnimatedVisibility(visible = signUpState is SignUpState.Error && (signUpState as SignUpState.Error).message != "Student number already exists") {
                            val error = (signUpState as? SignUpState.Error)?.message ?: ""
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { signUpViewModel.signUp(studentNumber, username, password, confirmPassword, selectedDepartment, selectedYearLevel) },
                            enabled = signUpState !is SignUpState.Loading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BeigePrimary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (signUpState is SignUpState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Sign Up", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onNavigateToLogin) {
                    Text("Already have an account? Login", color = BeigeSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
