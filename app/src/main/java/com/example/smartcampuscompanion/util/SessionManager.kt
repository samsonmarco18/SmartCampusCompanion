package com.example.smartcampuscompanion.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SmartCampusCompanion", Context.MODE_PRIVATE)

    companion object {
        const val USERNAME = "username"
        const val STUDENT_NUMBER = "student_number"
        const val DARK_MODE = "dark_mode"
    }

    fun saveSession(username: String, studentNumber: String) {
        prefs.edit {
            putString(USERNAME, username)
            putString(STUDENT_NUMBER, studentNumber)
        }
    }

    fun saveUsername(username: String) {
        prefs.edit {
            putString(USERNAME, username)
        }
    }

    fun fetchUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun fetchStudentNumber(): String? {
        return prefs.getString(STUDENT_NUMBER, null)
    }

    fun clearSession() {
        prefs.edit {
            remove(USERNAME)
            remove(STUDENT_NUMBER)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit {
            putBoolean(DARK_MODE, enabled)
        }
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(DARK_MODE, false)
    }
}
