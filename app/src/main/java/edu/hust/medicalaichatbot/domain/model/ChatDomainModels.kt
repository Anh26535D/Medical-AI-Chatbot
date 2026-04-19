package edu.hust.medicalaichatbot.domain.model

enum class MessageRole(val value: String) {
    USER("user"), 
    MODEL("model"), 
    ERROR("error")
}

enum class TriageTag(val level: Int) {
    RED(4), ORANGE(3), YELLOW(2), GREEN(1)
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
    val userId: String,
    val title: String,
    val lastUpdated: Long,
    val modelName: String,
    val summary: String? = null,
    val symptomCache: String? = null
)
