package com.example.smartcampuscompanion.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.User
import com.example.smartcampuscompanion.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(username: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _signUpState.value = SignUpState.Error("Passwords do not match")
            return
        }
        if (username.isBlank() || password.isBlank()) {
            _signUpState.value = SignUpState.Error("Username and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            val existingUser = userRepository.getUser(username)
            if (existingUser != null) {
                _signUpState.value = SignUpState.Error("Username already exists")
                return@launch
            }

            val passwordHash = MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }

            val newUser = User(username, passwordHash)
            userRepository.saveUser(newUser)
            _signUpState.value = SignUpState.Success
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}