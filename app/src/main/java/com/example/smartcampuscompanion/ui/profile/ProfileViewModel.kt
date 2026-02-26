package com.example.smartcampuscompanion.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Student
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ProfileViewModel(private val campusRepository: CampusRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val username = sessionManager.fetchUsername()
            if (username != null) {
                val student = campusRepository.getStudentByName(username)
                if (student != null) {
                    _uiState.value = ProfileState.Success(student)
                } else {
                    _uiState.value = ProfileState.Error("User not found")
                }
            } else {
                _uiState.value = ProfileState.Error("User not logged in")
            }
        }
    }

    fun updateUser(username: String, newUsername: String, newPassword: String) {
        viewModelScope.launch {
            val student = campusRepository.getStudentByName(username)
            if (student != null) {
                val passwordHash = if (newPassword.isNotBlank()) {
                    MessageDigest.getInstance("SHA-256")
                        .digest(newPassword.toByteArray())
                        .fold("") { str, it -> str + "%02x".format(it) }
                } else {
                    student.password
                }

                val updatedStudent = student.copy(
                    name = newUsername,
                    password = passwordHash
                )
                if (username != newUsername) {
                    campusRepository.dropStudent(student)
                }
                campusRepository.addStudent(updatedStudent)
                sessionManager.saveUsername(newUsername)
                _uiState.value = ProfileState.Success(updatedStudent)
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val student: Student) : ProfileState()
    data class Error(val message: String) : ProfileState()
}