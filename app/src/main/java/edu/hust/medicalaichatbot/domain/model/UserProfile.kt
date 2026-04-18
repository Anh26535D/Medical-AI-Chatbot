package edu.hust.medicalaichatbot.domain.model

data class UserProfile(
    val name: String = "Tôi",
    val age: String = "",
    val birthYear: String = "",
    val gender: String = "",
    val conditions: List<String> = emptyList(),
    val isInitial: Boolean = true
)
