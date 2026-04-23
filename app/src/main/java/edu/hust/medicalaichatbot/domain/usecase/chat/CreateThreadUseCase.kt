package edu.hust.medicalaichatbot.domain.usecase.chat

import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.repository.ChatRepository

class CreateThreadUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(thread: ChatThread) {
        repository.createThread(thread)
    }
}
