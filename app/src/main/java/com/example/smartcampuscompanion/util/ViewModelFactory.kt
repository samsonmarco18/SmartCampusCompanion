package com.example.smartcampuscompanion.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsViewModel
import com.example.smartcampuscompanion.ui.login.LoginViewModel
import com.example.smartcampuscompanion.ui.profile.ProfileViewModel
import com.example.smartcampuscompanion.ui.signup.SignUpViewModel
import com.example.smartcampuscompanion.ui.student_record.StudentRecordViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val campusRepository = CampusRepository(database.departmentDao())
        val sessionManager = SessionManager(application)

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(application, sessionManager) as T
        }
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(campusRepository, sessionManager) as T
        }
        if (modelClass.isAssignableFrom(AnnouncementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementsViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(StudentRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentRecordViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}