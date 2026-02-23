package com.example.smartcampuscompanion.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.repository.UserRepository
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginViewModel(private val userRepository: UserRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = userRepository.getUser(username.trim())
            if (user != null) {
                val passwordHash = MessageDigest.getInstance("SHA-256")
                    .digest(password.toByteArray())
                    .fold("") { str, it -> str + "%02x".format(it) }

                if (user.passwordHash == passwordHash) {
                    sessionManager.saveUsername(username.trim())
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid credentials")
                }
            } else {
                _loginState.value = LoginState.Error("Invalid credentials")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}