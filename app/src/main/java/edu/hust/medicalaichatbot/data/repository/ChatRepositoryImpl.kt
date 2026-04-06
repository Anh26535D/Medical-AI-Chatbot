package edu.hust.medicalaichatbot.data.repository

import android.util.Log
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
import edu.hust.medicalaichatbot.utils.Def
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val modelName: String
) : ChatRepository {

    private val TAG = Def.tagOf("Chat")

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

    override suspend fun getMessagesList(threadId: String): List<ChatMessage> {
        return chatDao.getMessagesByThread(threadId).map { it.toDomain() }
    }

    override fun getThreads(): Flow<List<ChatThread>> {
        return chatDao.getAllThreadsSortedByRecent().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private suspend fun ensureThreadExists(threadId: String) {
        val existingThread = chatDao.getThreadById(threadId)
        if (existingThread == null) {
            Log.d(TAG, "ensureThreadExists: Thread $threadId not found. Creating it...")
            chatDao.insertThread(
                edu.hust.medicalaichatbot.data.local.entity.ChatThread(
                    threadId = threadId,
                    title = "New Chat",
                    modelName = modelName,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        Log.d(TAG, "sendMessage: content='${message.content}', threadId=${message.threadId}")
        return try {
            ensureThreadExists(message.threadId)
            chatDao.insertMessageAndUpdateThread(message.toEntity())
            Log.d(TAG, "sendMessage: Success")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendMessage: Failed", e)
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
        Log.d(TAG, "getAiResponse: prompt='$prompt', historySize=${history.size}")
        return try {
            val historyContent = history.map { 
                content(role = if (it.role.name == "USER") "user" else "model") { text(it.content) }
            }
            Log.d(TAG, "getAiResponse: Starting chat session with model '$modelName'...")
            val chatSession = generativeModel.startChat(history = historyContent)
            
            Log.d(TAG, "getAiResponse: Sending message to Gemini...")
            val response = chatSession.sendMessage(prompt)
            
            val responseText = response.text
            Log.i(TAG, "getAiResponse: Received response: '$responseText'")
            
            if (responseText == null) {
                Log.w(TAG, "getAiResponse: Response text is NULL")
            }
            
            Result.success(responseText ?: "No response from AI")
        } catch (e: Exception) {
            Log.e(TAG, "getAiResponse: Error calling Gemini API", e)
            Result.failure(e)
        }
    }
}
