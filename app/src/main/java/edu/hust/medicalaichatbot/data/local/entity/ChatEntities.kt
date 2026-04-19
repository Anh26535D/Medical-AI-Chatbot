package edu.hust.medicalaichatbot.data.local.entity

import androidx.room.*

@Entity(tableName = "chat_threads")
data class ChatThread(
    @PrimaryKey val threadId: String,
    val userId: String, // "guest" or user phone/id
    val title: String,
    val lastUpdated: Long,
    val modelName: String,
    val summary: String? = null,
    val symptomCache: String? = null
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatThread::class,
            parentColumns = ["threadId"],
            childColumns = ["threadOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("threadOwnerId")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val threadOwnerId: String,
    val content: String,
    val role: String, // "user" or "assistant"
    val timestamp: Long,
    val status: String // "sending", "sent", "error"
)

data class ChatThreadWithMessages(
    @Embedded val thread: ChatThread,
    @Relation(
        parentColumn = "threadId",
        entityColumn = "threadOwnerId"
    )
    val messages: List<ChatMessageEntity>
)
