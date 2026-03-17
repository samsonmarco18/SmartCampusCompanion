package com.example.smartcampuscompanion.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AnnouncementRepository
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SignUpViewModel(
    application: Application,
    private val campusRepository: CampusRepository,
    private val announcementRepository: AnnouncementRepository
) : AndroidViewModel(application) {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(studentNumber: String, username: String, password: String, confirmPassword: String, department: String?, yearLevel: String?) {
        if (password != confirmPassword) {
            _signUpState.value = SignUpState.Error("Passwords do not match")
            return
        }
        if (studentNumber.isBlank() || username.isBlank() || password.isBlank() || department == null || yearLevel == null) {
            _signUpState.value = SignUpState.Error("All fields must be filled")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            val existingStudent = campusRepository.getStudentByStudentNumber(studentNumber)
            if (existingStudent != null) {
                val announcement = Announcement(
                    title = "Security Alert: Account Validation Required",
                    content = "Someone attempted to create an account with your student number. Please go to MSID to validate your account and provide a valid ID to prove ownership.",
                    category = "Urgent",
                    departmentName = null,
                    dueDate = System.currentTimeMillis(),
                    studentNumber = studentNumber
                )
                announcementRepository.insertAnnouncement(announcement)
                _signUpState.value = SignUpState.Error("Student number already exists")
                return@launch
            }

            val passwordHash = MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }

            val newStudent = Student(
                studentNumber = studentNumber,
                name = username,
                password = passwordHash,
                email = "", // You may want to add an email field to the sign up screen
                yearLevel = yearLevel,
                departmentName = department,
            )
            campusRepository.addStudent(newStudent)
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
