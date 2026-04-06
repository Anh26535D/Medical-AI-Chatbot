package edu.hust.medicalaichatbot.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import edu.hust.medicalaichatbot.data.local.dao.ChatDao
import edu.hust.medicalaichatbot.data.mapper.toDomain
import edu.hust.medicalaichatbot.data.mapper.toEntity
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val modelName: String
) : ChatRepository {

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName)

    override fun getMessages(threadId: String): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { chatDao.getMessagesForThreadPaging(threadId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getThreads(): Flow<List<ChatThread>> {
        return chatDao.getAllThreadsSortedByRecent().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            chatDao.insertMessageAndUpdateThread(message.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createThread(thread: ChatThread) {
        chatDao.insertThread(thread.toEntity())
    }

    override suspend fun deleteThread(threadId: String) {
        chatDao.deleteThread(threadId)
    }

    override suspend fun getAiResponse(prompt: String, history: List<ChatMessage>): Result<String> {
        return try {
            val historyContent = history.map { 
                content(role = if (it.role.name == "USER") "user" else "model") { text(it.content) }
            }
            val chatSession = generativeModel.startChat(history = historyContent)
            val response = chatSession.sendMessage(prompt)
            Result.success(response.text ?: "No response from AI")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
