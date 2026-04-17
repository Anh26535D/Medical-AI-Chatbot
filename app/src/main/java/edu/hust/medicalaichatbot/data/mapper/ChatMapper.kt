package edu.hust.medicalaichatbot.data.mapper

import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread as ChatThreadEntity
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.model.MessageRole

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        threadId = threadOwnerId,
        content = content,
        role = when (role) {
            "user" -> MessageRole.USER
            "assistant" -> MessageRole.ASSISTANT
            else -> MessageRole.ERROR
        },
        timestamp = timestamp,
        status = status
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        threadOwnerId = threadId,
        content = content,
        role = role.name.lowercase(),
        timestamp = timestamp,
        status = status
    )
}

fun ChatThreadEntity.toDomain(): ChatThread {
    return ChatThread(
        id = threadId,
        title = title,
        lastUpdated = lastUpdated,
        modelName = modelName,
        summary = summary
    )
}

fun ChatThread.toEntity(): ChatThreadEntity {
    return ChatThreadEntity(
        threadId = id,
        title = title,
        lastUpdated = lastUpdated,
        modelName = modelName,
        summary = summary
    )
}
