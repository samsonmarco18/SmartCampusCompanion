package com.example.smartcampuscompanion.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginViewModel(application: Application, private val sessionManager: SessionManager) : AndroidViewModel(application) {

    private val campusRepository: CampusRepository
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        val departmentDao = AppDatabase.getDatabase(application).departmentDao()
        campusRepository = CampusRepository(departmentDao)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val student = campusRepository.getStudentByName(username.trim())
            if (student != null) {
                val passwordHash = MessageDigest.getInstance("SHA-256")
                    .digest(password.toByteArray())
                    .fold("") { str, it -> str + "%02x".format(it) }

                if (student.password == passwordHash) {
                    sessionManager.saveSession(student.name, student.studentNumber, student.role)
                    _loginState.value = LoginState.Success(student.adminMessage)
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
    data class Success(val adminMessage: String? = null) : LoginState()
    data class Error(val message: String) : LoginState()
}
