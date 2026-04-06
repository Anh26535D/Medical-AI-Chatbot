package edu.hust.medicalaichatbot.domain.usecase.chat

import androidx.paging.PagingData
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(threadId: String): Flow<PagingData<ChatMessage>> {
        return repository.getMessages(threadId)
    }
}
