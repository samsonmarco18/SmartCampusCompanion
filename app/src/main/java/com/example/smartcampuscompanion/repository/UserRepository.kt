package com.example.smartcampuscompanion.repository

import android.content.Context
import com.example.smartcampuscompanion.data.User
import com.google.gson.Gson

class UserRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(user.username, userJson).apply()
    }

    fun getUser(username: String): User? {
        val userJson = sharedPreferences.getString(username, null)
        return gson.fromJson(userJson, User::class.java)
    }

    fun deleteUser(username: String) {
        sharedPreferences.edit().remove(username).apply()
    }
}