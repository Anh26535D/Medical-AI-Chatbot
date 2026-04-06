package edu.hust.medicalaichatbot.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import edu.hust.medicalaichatbot.data.local.dao.ChatDao
import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {

    fun getMessages(threadId: String): Flow<PagingData<ChatMessageEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { chatDao.getMessagesForThreadPaging(threadId) }
        ).flow
    }

    suspend fun saveMessage(message: ChatMessageEntity) {
        chatDao.insertMessageAndUpdateThread(message)
    }

    suspend fun createThread(thread: ChatThread) {
        chatDao.insertThread(thread)
    }

    fun getAllThreads(): Flow<List<ChatThread>> {
        return chatDao.getAllThreadsSortedByRecent()
    }

    suspend fun deleteThread(threadId: String) {
        chatDao.deleteThread(threadId)
    }
}
