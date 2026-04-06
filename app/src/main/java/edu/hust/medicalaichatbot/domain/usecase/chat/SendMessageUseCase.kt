package edu.hust.medicalaichatbot.domain.usecase.chat

import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import java.util.UUID

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(threadId: String, text: String): Result<Unit> {
        val userMessage = ChatMessage(
            threadId = threadId,
            content = text,
            role = MessageRole.USER,
            timestamp = System.currentTimeMillis(),
            status = "sent"
        )
        
        repository.sendMessage(userMessage)
        
        // Lấy lịch sử chat để làm ngữ cảnh cho AI
        val history = repository.getMessagesList(threadId)
        val responseResult = repository.getAiResponse(text, history)
        
        return responseResult.mapCatching { aiText ->
            val assistantMessage = ChatMessage(
                threadId = threadId,
                content = aiText,
                role = MessageRole.ASSISTANT,
                timestamp = System.currentTimeMillis(),
                status = "sent"
            )
            repository.sendMessage(assistantMessage)
            Unit
        }.onFailure {
            val errorMessage = ChatMessage(
                threadId = threadId,
                content = "Error processing request",
                role = MessageRole.ERROR,
                timestamp = System.currentTimeMillis(),
                status = "error"
            )
            repository.sendMessage(errorMessage)
        }
    }
}
