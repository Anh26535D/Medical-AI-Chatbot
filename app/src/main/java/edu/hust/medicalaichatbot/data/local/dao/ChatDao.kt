package edu.hust.medicalaichatbot.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ChatThread)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Update
    suspend fun updateThread(thread: ChatThread)

    @Query("SELECT * FROM chat_threads WHERE threadId = :threadId")
    suspend fun getThreadById(threadId: String): ChatThread?

    @Transaction
    suspend fun insertMessageAndUpdateThread(message: ChatMessageEntity) {
        insertMessage(message)
        val thread = getThreadById(message.threadOwnerId)
        thread?.let {
            updateThread(it.copy(lastUpdated = message.timestamp))
        }
    }

    @Query("SELECT * FROM chat_threads ORDER BY lastUpdated DESC")
    fun getAllThreadsSortedByRecent(): Flow<List<ChatThread>>

    @Query("SELECT * FROM chat_messages WHERE threadOwnerId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThreadPaging(threadId: String): PagingSource<Int, ChatMessageEntity>

    @Query("DELETE FROM chat_threads WHERE threadId = :threadId")
    suspend fun deleteThread(threadId: String)
}
