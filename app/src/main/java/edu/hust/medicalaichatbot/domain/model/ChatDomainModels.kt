package edu.hust.medicalaichatbot.domain.model

enum class MessageRole {
    USER, ASSISTANT, ERROR
}

data class ChatMessage(
    val id: Long = 0,
    val threadId: String,
    val content: String,
    val role: MessageRole,
    val timestamp: Long,
    val status: String
)

data class ChatThread(
    val id: String,
    val title: String,
    val lastUpdated: Long,
    val modelName: String
)
