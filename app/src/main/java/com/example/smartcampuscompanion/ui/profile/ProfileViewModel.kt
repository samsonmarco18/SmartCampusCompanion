package com.example.smartcampuscompanion.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.User
import com.example.smartcampuscompanion.repository.UserRepository
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ProfileViewModel(private val userRepository: UserRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val username = sessionManager.fetchUsername()
            if (username != null) {
                val user = userRepository.getUser(username)
                if (user != null) {
                    _uiState.value = ProfileState.Success(user)
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
            val user = userRepository.getUser(username)
            if (user != null) {
                val passwordHash = if (newPassword.isNotBlank()) {
                    MessageDigest.getInstance("SHA-256")
                        .digest(newPassword.toByteArray())
                        .fold("") { str, it -> str + "%02x".format(it) }
                } else {
                    user.passwordHash
                }

                val updatedUser = user.copy(
                    username = newUsername,
                    passwordHash = passwordHash
                )
                if (username != newUsername) {
                    userRepository.deleteUser(username)
                }
                userRepository.saveUser(updatedUser)
                sessionManager.saveUsername(newUsername)
                _uiState.value = ProfileState.Success(updatedUser)
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}