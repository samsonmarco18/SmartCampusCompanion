package com.example.smartcampuscompanion.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SmartCampusCompanion", Context.MODE_PRIVATE)

    companion object {
        const val USERNAME = "username"
        const val DARK_MODE = "dark_mode"
    }

    fun saveUsername(username: String) {
        prefs.edit {
            putString(USERNAME, username)
        }
    }

    fun fetchUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun clearUsername() {
        prefs.edit {
            remove(USERNAME)
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
