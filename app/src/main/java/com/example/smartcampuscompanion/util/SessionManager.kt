package com.example.smartcampuscompanion.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SmartCampusCompanion", Context.MODE_PRIVATE)

    companion object {
        const val USERNAME = "username"
        const val STUDENT_NUMBER = "student_number"
        const val ROLE = "role"
        const val PROFILE_IMAGE_URL = "profile_image_url"
        const val DARK_MODE = "dark_mode"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    fun saveSession(username: String, studentNumber: String, role: String, profileImageUrl: String? = null) {
        prefs.edit {
            putString(USERNAME, username)
            putString(STUDENT_NUMBER, studentNumber)
            putString(ROLE, role)
            putString(PROFILE_IMAGE_URL, profileImageUrl)
        }
    }

    fun saveUsername(username: String) {
        prefs.edit {
            putString(USERNAME, username)
        }
    }

    fun saveProfileImageUrl(url: String?) {
        prefs.edit {
            putString(PROFILE_IMAGE_URL, url)
        }
    }

    fun fetchUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun fetchStudentNumber(): String? {
        return prefs.getString(STUDENT_NUMBER, null)
    }

    fun fetchRole(): String? {
        return prefs.getString(ROLE, null)
    }

    fun fetchProfileImageUrl(): String? {
        return prefs.getString(PROFILE_IMAGE_URL, null)
    }

    fun clearSession() {
        prefs.edit {
            remove(USERNAME)
            remove(STUDENT_NUMBER)
            remove(ROLE)
            remove(PROFILE_IMAGE_URL)
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

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(NOTIFICATIONS_ENABLED, enabled)
        }
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(NOTIFICATIONS_ENABLED, true)
    }
}
