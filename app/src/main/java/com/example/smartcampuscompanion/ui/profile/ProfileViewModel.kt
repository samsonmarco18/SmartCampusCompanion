package com.example.smartcampuscompanion.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.Student
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val campusRepository: CampusRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                    if (userDoc.exists()) {
                        val student = userDoc.toObject(Student::class.java)
                        if (student != null) {
                            _uiState.value = ProfileState.Success(student)
                            // Keep session manager in sync
                            sessionManager.saveProfileImageUrl(student.profileImageUrl)
                            sessionManager.saveUsername(student.name)
                        } else {
                            _uiState.value = ProfileState.Error("Failed to parse user data")
                        }
                    } else {
                        _uiState.value = ProfileState.Error("User record not found in database")
                    }
                } catch (e: Exception) {
                    _uiState.value = ProfileState.Error(e.localizedMessage ?: "Failed to load profile")
                }
            }
        } else {
            _uiState.value = ProfileState.Error("User not logged in")
        }
    }

    fun updateUserProfileImage(imageUrl: String) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(currentUser.uid)
                    .update("profileImageUrl", imageUrl).await()
                sessionManager.saveProfileImageUrl(imageUrl)
                // Refresh local state
                loadUserProfile()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateUser(newUsername: String, newPassword: String? = null) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                // Update Firebase Auth password if provided
                if (!newPassword.isNullOrBlank()) {
                    currentUser.updatePassword(newPassword).await()
                }

                // Update Firestore record
                val updates = mutableMapOf<String, Any>(
                    "name" to newUsername
                )
                if (!newPassword.isNullOrBlank()) {
                    updates["password"] = newPassword
                }

                firestore.collection("users").document(currentUser.uid).update(updates).await()
                
                // Update session manager
                sessionManager.saveUsername(newUsername)
                
                // Refresh local state
                loadUserProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.localizedMessage ?: "Update failed")
            }
        }
    }
    
    fun logout() {
        auth.signOut()
        sessionManager.clearSession()
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val student: Student) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
