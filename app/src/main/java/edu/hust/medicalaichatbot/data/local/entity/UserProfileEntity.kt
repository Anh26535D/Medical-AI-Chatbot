package edu.hust.medicalaichatbot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // phoneNumber or "guest"
    val name: String,
    val birthYear: String,
    val age: String,
    val gender: String,
    val conditions: String, // Store as comma-separated string
    val isPrimary: Boolean
)
