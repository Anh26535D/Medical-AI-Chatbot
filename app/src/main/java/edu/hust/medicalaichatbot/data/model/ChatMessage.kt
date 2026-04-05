package edu.hust.medicalaichatbot.data.model

enum class MessageRole {
    USER, AI
}

data class ChatMessage(
    val text: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis()
)
