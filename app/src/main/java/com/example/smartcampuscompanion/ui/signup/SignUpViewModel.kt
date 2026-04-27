package com.example.smartcampuscompanion.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel(application: Application, private val sessionManager: SessionManager) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(
        studentNumber: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        department: String?,
        yearLevel: String?
    ) {
        if (password != confirmPassword) {
            _signUpState.value = SignUpState.Error("Passwords do not match")
            return
        }
        if (studentNumber.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() || department == null || yearLevel == null) {
            _signUpState.value = SignUpState.Error("All fields must be filled")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                // Check if student number exists in Firestore
                val studentNumberQuery = firestore.collection("users")
                    .whereEqualTo("studentNumber", studentNumber)
                    .get()
                    .await()

                if (!studentNumberQuery.isEmpty) {
                    _signUpState.value = SignUpState.Error("Student number already exists")
                    return@launch
                }

                // Create user in Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val user = authResult.user

                if (user != null) {
                    val fcmToken = try {
                        FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {
                        null
                    }

                    // Store additional user info in Firestore
                    val userData = hashMapOf(
                        "uid" to user.uid,
                        "studentNumber" to studentNumber,
                        "name" to username,
                        "email" to email.trim(),
                        "yearLevel" to yearLevel,
                        "departmentName" to department,
                        "role" to "student",
                        "status" to "Regular",
                        "fcmToken" to fcmToken,
                        "profileImageUrl" to null
                    )

                    firestore.collection("users").document(user.uid).set(userData).await()
                    
                    // Save session data locally
                    sessionManager.saveSession(username, studentNumber, "student", null)
                    
                    _signUpState.value = SignUpState.Success
                } else {
                    _signUpState.value = SignUpState.Error("Sign up failed")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    fun completeGoogleSignUp(
        studentNumber: String,
        username: String,
        email: String,
        department: String?,
        yearLevel: String?
    ) {
        if (studentNumber.isBlank() || username.isBlank() || department == null || yearLevel == null) {
            _signUpState.value = SignUpState.Error("All fields must be filled")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val user = auth.currentUser
                if (user != null) {
                    // Check if student number exists
                    val studentNumberQuery = firestore.collection("users")
                        .whereEqualTo("studentNumber", studentNumber)
                        .get()
                        .await()

                    if (!studentNumberQuery.isEmpty) {
                        _signUpState.value = SignUpState.Error("Student number already exists")
                        return@launch
                    }

                    val fcmToken = try {
                        FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {
                        null
                    }

                    val userData = hashMapOf(
                        "uid" to user.uid,
                        "studentNumber" to studentNumber,
                        "name" to username,
                        "email" to email.trim(),
                        "yearLevel" to yearLevel,
                        "departmentName" to department,
                        "role" to "student",
                        "status" to "Regular",
                        "fcmToken" to fcmToken,
                        "profileImageUrl" to user.photoUrl?.toString()
                    )

                    firestore.collection("users").document(user.uid).set(userData).await()
                    
                    // Save session data locally
                    sessionManager.saveSession(username, studentNumber, "student", user.photoUrl?.toString())

                    _signUpState.value = SignUpState.Success
                } else {
                    _signUpState.value = SignUpState.Error("No authenticated user found")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}
