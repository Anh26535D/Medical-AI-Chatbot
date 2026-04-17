package edu.hust.medicalaichatbot.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_threads ORDER BY lastUpdated DESC")
    fun getAllThreadsSortedByRecent(): Flow<List<ChatThread>>

    @Query("SELECT * FROM chat_threads WHERE threadId = :threadId")
    suspend fun getThreadById(threadId: String): ChatThread?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ChatThread)

    @Query("DELETE FROM chat_threads WHERE threadId = :threadId")
    suspend fun deleteThread(threadId: String)

    @Query("SELECT * FROM chat_messages WHERE threadOwnerId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThreadPaging(threadId: String): PagingSource<Int, ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE threadOwnerId = :threadId ORDER BY timestamp ASC")
    suspend fun getMessagesByThread(threadId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Transaction
    suspend fun insertMessageAndUpdateThread(message: ChatMessageEntity) {
        insertMessage(message)
        updateThreadTimestamp(message.threadOwnerId, message.timestamp)
    }

    @Query("UPDATE chat_threads SET lastUpdated = :timestamp WHERE threadId = :threadId")
    suspend fun updateThreadTimestamp(threadId: String, timestamp: Long)

    @Query("UPDATE chat_threads SET summary = :summary WHERE threadId = :threadId")
    suspend fun updateThreadSummary(threadId: String, summary: String)
}
