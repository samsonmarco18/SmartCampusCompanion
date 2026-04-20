package com.example.smartcampuscompanion.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(application: Application, private val sessionManager: SessionManager) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
                val user = authResult.user
                
                if (user != null) {
                    val userDoc = firestore.collection("users").document(user.uid).get().await()
                    if (userDoc.exists()) {
                        val name = userDoc.getString("name") ?: ""
                        val studentNumber = userDoc.getString("studentNumber") ?: ""
                        val role = userDoc.getString("role") ?: "student"
                        val adminMessage = userDoc.getString("adminMessage")
                        
                        sessionManager.saveSession(name, studentNumber, role)
                        _loginState.value = LoginState.Success(adminMessage)
                    } else {
                        _loginState.value = LoginState.Error("User data not found")
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Invalid credentials")
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
