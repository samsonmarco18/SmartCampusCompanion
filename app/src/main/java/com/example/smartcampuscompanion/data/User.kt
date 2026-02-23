package com.example.smartcampuscompanion.data

data class User(
    val username: String,
    val passwordHash: String,
    val department: String? = null, // Static for now, can be made dynamic later
    val yearLevel: String? = null // Static for now, can be made dynamic later
)