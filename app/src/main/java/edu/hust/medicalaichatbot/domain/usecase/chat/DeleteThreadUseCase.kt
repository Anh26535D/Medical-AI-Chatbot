package edu.hust.medicalaichatbot.domain.usecase.chat

import edu.hust.medicalaichatbot.domain.repository.ChatRepository

class DeleteThreadUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(threadId: String) {
        repository.deleteThread(threadId)
    }
}
