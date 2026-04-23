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
import edu.hust.medicalaichatbot.BuildConfig
import edu.hust.medicalaichatbot.data.local.dao.ChatDao
import edu.hust.medicalaichatbot.data.mapper.toDomain
import edu.hust.medicalaichatbot.data.mapper.toEntity
import edu.hust.medicalaichatbot.data.service.LocationService
import edu.hust.medicalaichatbot.data.service.MedicalPlaceSearcher
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.repository.ChatRepository
import edu.hust.medicalaichatbot.data.llm.ChatManager
import edu.hust.medicalaichatbot.data.llm.SummaryManager
import edu.hust.medicalaichatbot.data.llm.parser.ChatResponseParser
import edu.hust.medicalaichatbot.utils.Constants
import edu.hust.medicalaichatbot.utils.Def
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import edu.hust.medicalaichatbot.utils.LocationUtils
import java.util.Locale

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val modelName: String,
    private val locationService: LocationService? = null,
    private val placeSearcher: MedicalPlaceSearcher = MedicalPlaceSearcher()
) : ChatRepository {

    companion object {
        private val TAG = Def.tagOf("ChatRepo")
    }

    private val chatModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = modelName,
            systemInstruction = content { text(BuildConfig.CHAT_SYSTEM_PROMPT) }
        )

    private val summaryModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = modelName,
            systemInstruction = content { text(BuildConfig.SUMMARY_SYSTEM_PROMPT) }
        )

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

    override fun getThreads(userId: String): Flow<List<ChatThread>> {
        return chatDao.getThreadsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private suspend fun ensureThreadExists(threadId: String, userId: String = "guest") {
        val existingThread = chatDao.getThreadById(threadId)
        if (existingThread == null) {
            chatDao.insertThread(
                edu.hust.medicalaichatbot.data.local.entity.ChatThread(
                    threadId = threadId,
                    userId = userId,
                    title = "Cuộc trò chuyện mới",
                    modelName = modelName,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            // Note: This logic might need refinement if threadId is provided but userId is unknown here
            // For now, if it exists, it's fine. If not, it defaults to guest which might be wrong if 
            // the user is logged in. 
            // Better to have ensureThreadExists called explicitly with userId before sendMessage if possible.
            val existing = chatDao.getThreadById(message.threadId)
            if (existing == null) {
                ensureThreadExists(message.threadId)
            }
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
                        "- ${it.name} (${String.format(Locale.ROOT, "%.1f", LocationUtils.calculateDistance(location.latitude, location.longitude, it.lat, it.lon))} km) - ${it.address}"
                    }
                }
            }

            // 2. Lấy thông tin thread và summary
            val thread = chatDao.getThreadById(threadId)
            val currentSummary = thread?.summary
            val symptomCache = thread?.symptomCache

            // 3. Chuyển đổi history
            val historyContent = history.map { 
                content(role = if (it.role == MessageRole.USER) Constants.ROLE_USER else Constants.ROLE_MODEL) { text(it.content) }
            }

            val chatManager = ChatManager(chatModel, historyContent.takeLast(12))
            val summaryManager = SummaryManager(summaryModel)
            
            // 4. Gửi tin nhắn với Location Context, Medical Summary và Symptom Cache
            val response = chatManager.sendMessage(
                prompt = prompt, 
                currentSummary = currentSummary,
                nearbyPlaces = nearbyPlacesString,
                symptomCache = symptomCache
            )
            val responseText = response.text ?: "Xin lỗi, tôi không thể xử lý yêu cầu lúc này."

            // 5. Cập nhật Cache triệu chứng và Nén ngữ cảnh tự động
            val parsedResponse = ChatResponseParser.parse(responseText)
            
            chatManager.extractSymptomCache()?.let { newCache ->
                chatDao.updateThreadSymptomCache(threadId, newCache)
            }

            // Generate or update summary if compression is needed OR if a diagnosis guess is provided
            if (chatManager.shouldCompress() || (parsedResponse.diagnosisGuess != null && thread?.summary == null)) {
                summaryManager.generateSummary(history, parsedResponse.triageTag?.name)?.let { newSummaryResponse ->
                    chatDao.updateThreadSummary(threadId, newSummaryResponse.rawText)
                }
            } else if (parsedResponse.diagnosisGuess != null) {
                summaryManager.generateSummary(history, parsedResponse.triageTag?.name)?.let { newSummaryResponse ->
                    chatDao.updateThreadSummary(threadId, newSummaryResponse.rawText)
                }
            }
            
            Result.success(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "getAiResponse Error", e)
            Result.failure(e)
        }
    }
}
