package com.example.smartcampuscompanion.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SmartCampusCompanion", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val DARK_MODE = "dark_mode"
    }

    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(USER_TOKEN, token)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun clearAuthToken() {
        prefs.edit {
            remove(USER_TOKEN)
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
