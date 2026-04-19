package edu.hust.medicalaichatbot.domain.repository

import androidx.paging.PagingData
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(threadId: String): Flow<PagingData<ChatMessage>>
    suspend fun getMessagesList(threadId: String): List<ChatMessage>
    fun getThreads(userId: String): Flow<List<ChatThread>>
    suspend fun sendMessage(message: ChatMessage): Result<Unit>
    suspend fun createThread(thread: ChatThread)
    suspend fun deleteThread(threadId: String)
    suspend fun getAiResponse(prompt: String, history: List<ChatMessage>): Result<String>
}
