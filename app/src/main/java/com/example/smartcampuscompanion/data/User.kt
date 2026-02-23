package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val studentNumber: String,
    val username: String,
    val passwordHash: String,
    val department: String,
    val yearLevel: String
)
