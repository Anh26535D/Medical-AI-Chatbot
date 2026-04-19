package edu.hust.medicalaichatbot.data.mapper

import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread as ChatThreadEntity
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.utils.Constants

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        threadId = threadOwnerId,
        content = content,
        role = when (role) {
            Constants.ROLE_USER -> MessageRole.USER
            Constants.ROLE_MODEL -> MessageRole.MODEL
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
        role = role.value,
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
        summary = summary,
        symptomCache = symptomCache
    )
}

fun ChatThread.toEntity(): ChatThreadEntity {
    return ChatThreadEntity(
        threadId = id,
        title = title,
        lastUpdated = lastUpdated,
        modelName = modelName,
        summary = summary,
        symptomCache = symptomCache
    )
}
