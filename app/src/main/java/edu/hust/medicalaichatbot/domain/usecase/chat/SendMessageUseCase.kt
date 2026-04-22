package edu.hust.medicalaichatbot.domain.usecase.chat

import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import java.util.UUID

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(threadId: String, text: String, userId: String = "guest"): Result<Unit> {
        val userMessage = ChatMessage(
            threadId = threadId,
            content = text,
            role = MessageRole.USER,
            timestamp = System.currentTimeMillis(),
            status = "sent"
        )
        
        // Ensure thread exists with the correct userId
        val existingThreads = repository.getThreads(userId)
        // This is a bit tricky with Flow, but since sendMessage is suspend, 
        // we might want to check if the thread exists in the repository.
        // For simplicity, let's assume the repository handles thread creation correctly 
        // if we pass userId to a modified sendMessage or similar.
        
        repository.sendMessage(userMessage)

        // Bắt lỗi Local: Fallback khi text rác, quá ngắn hoặc toàn số/ký tự đặc biệt
        val trimmedText = text.trim()
        val hasNoLetters = trimmedText.none { it.isLetter() }
        if (trimmedText.length <= 2 || hasNoLetters) {
            val fallbackMessage = ChatMessage(
                threadId = threadId,
                content = "<message>Xin lỗi, tôi chưa hiểu rõ vấn đề của bạn. Bạn có thể mô tả cụ thể triệu chứng sức khỏe hoặc tình trạng cơ thể để tôi hỗ trợ chính xác hơn được không?</message>",
                role = MessageRole.MODEL,
                timestamp = System.currentTimeMillis(),
                status = "sent"
            )
            repository.sendMessage(fallbackMessage)
            return Result.success(Unit)
        }

        // Lấy lịch sử chat để làm ngữ cảnh cho AI
        val history = repository.getMessagesList(threadId)
        val responseResult = repository.getAiResponse(text, history)
        
        return responseResult.mapCatching { aiText ->
            val assistantMessage = ChatMessage(
                threadId = threadId,
                content = aiText,
                role = MessageRole.MODEL,
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
