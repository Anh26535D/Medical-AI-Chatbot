package edu.hust.medicalaichatbot.domain.usecase.chat

import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetThreadsUseCase(private val repository: ChatRepository) {
    operator fun invoke(): Flow<List<ChatThread>> {
        return repository.getThreads()
    }
}
