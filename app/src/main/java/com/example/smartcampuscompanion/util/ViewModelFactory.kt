package com.example.smartcampuscompanion.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.data.AnnouncementRepository
import com.example.smartcampuscompanion.data.AppDatabase
import com.example.smartcampuscompanion.data.CampusRepository
import com.example.smartcampuscompanion.data.TaskRepository
import com.example.smartcampuscompanion.ui.announcements.AnnouncementsViewModel
import com.example.smartcampuscompanion.ui.announcement_manager.AnnouncementManagerViewModel
import com.example.smartcampuscompanion.ui.login.LoginViewModel
import com.example.smartcampuscompanion.ui.profile.ProfileViewModel
import com.example.smartcampuscompanion.ui.signup.SignUpViewModel
import com.example.smartcampuscompanion.ui.student_record.StudentRecordViewModel
import com.example.smartcampuscompanion.ui.task_manager.TaskViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val campusRepository = CampusRepository(database.departmentDao())
        val taskRepository = TaskRepository(database.taskDao())
        val announcementRepository = AnnouncementRepository(database.announcementDao(), database.commentDao())
        val sessionManager = SessionManager(application)

        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(application, sessionManager, campusRepository) as T
            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->
                SignUpViewModel(application, campusRepository, announcementRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(campusRepository, sessionManager) as T
            modelClass.isAssignableFrom(AnnouncementsViewModel::class.java) ->
                AnnouncementsViewModel(application, announcementRepository, sessionManager) as T
            modelClass.isAssignableFrom(AnnouncementManagerViewModel::class.java) ->
                AnnouncementManagerViewModel(application, announcementRepository) as T
            modelClass.isAssignableFrom(StudentRecordViewModel::class.java) ->
                StudentRecordViewModel(application, campusRepository) as T
            modelClass.isAssignableFrom(TaskViewModel::class.java) ->
                TaskViewModel(application, taskRepository, sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
