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
import edu.hust.medicalaichatbot.data.service.LocationService
import edu.hust.medicalaichatbot.data.service.MedicalPlaceSearcher
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import edu.hust.medicalaichatbot.utils.Def
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val modelName: String,
    private val locationService: LocationService? = null,
    private val placeSearcher: MedicalPlaceSearcher = MedicalPlaceSearcher()
) : ChatRepository {

    companion object {
        private val TAG = Def.tagOf("ChatRepo")
    }

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
            chatDao.insertThread(
                edu.hust.medicalaichatbot.data.local.entity.ChatThread(
                    threadId = threadId,
                    title = "Cuộc trò chuyện mới",
                    modelName = modelName,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            ensureThreadExists(message.threadId)
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

    override suspend fun getAiResponse(prompt: String, history: List<ChatMessage>): Result<String> = withContext(Dispatchers.IO) {
        val threadId = if (history.isNotEmpty()) history.first().threadId else "default_thread"
        
        try {
            // 1. Lấy vị trí và tìm địa điểm gần nhất (nếu có yêu cầu hoặc mặc định)
            var nearbyPlacesString: String? = null
            locationService?.getCurrentLocation()?.let { location ->
                val places = placeSearcher.findNearbyPlaces(location.latitude, location.longitude)
                if (places.isNotEmpty()) {
                    nearbyPlacesString = places.joinToString("\n") { 
                        "- ${it.name} (${String.format("%.1f", calculateDist(location.latitude, location.longitude, it.lat, it.lon))} km) - ${it.address}"
                    }
                }
            }

            // 2. Lấy thông tin thread và summary
            val thread = chatDao.getThreadById(threadId)
            val currentSummary = thread?.summary

            // 3. Chuyển đổi history
            val historyContent = history.map { 
                content(role = if (it.role == MessageRole.USER) "user" else "model") { text(it.content) }
            }
            
            val chatManager = MedicalChatManager(generativeModel, historyContent)
            
            // 4. Gửi tin nhắn với Location Context và Medical Summary
            val response = chatManager.sendMessage(
                prompt = prompt, 
                currentSummary = currentSummary,
                nearbyPlaces = nearbyPlacesString
            )
            val responseText = response.text ?: "Xin lỗi, tôi không thể xử lý yêu cầu lúc này."

            // 5. Nén ngữ cảnh tự động
            if (chatManager.shouldCompress()) {
                chatManager.requestMedicalSummary()?.let { newSummary ->
                    chatDao.updateThreadSummary(threadId, newSummary)
                }
            }
            
            Result.success(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "getAiResponse Error", e)
            Result.failure(e)
        }
    }

    private fun calculateDist(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
