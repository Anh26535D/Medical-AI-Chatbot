package edu.hust.medicalaichatbot.data.llm

import android.util.Log
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.content
import edu.hust.medicalaichatbot.BuildConfig
import edu.hust.medicalaichatbot.utils.Constants
import edu.hust.medicalaichatbot.utils.Def

class ChatManager(
    private val model: GenerativeModel,
    initialHistory: List<Content> = emptyList()
) {
    private val TAG = Def.tagOf("ChatManager")
    private val _history = initialHistory.toMutableList()
    val history: List<Content> get() = _history

    fun shouldCompress(): Boolean {
        val totalChars = _history.sumOf { content -> 
            content.parts.sumOf { part -> part.toString().length } 
        }
        return _history.size >= 10 || totalChars > 4000
    }

    suspend fun extractSymptomCache(): String? {
        val cachePrompt = content(role = Constants.ROLE_USER) {
            text(BuildConfig.SYMPTOM_CACHE_PROMPT)
        }

        val requestList = mutableListOf<Content>()
        requestList.addAll(_history)
        requestList.add(cachePrompt)

        return try {
            val response = model.generateContent(requestList)
            val result = response.text?.trim()?.removeSurrounding("```json", "```")?.trim()
            if (result == null || result == "[]" || result.isEmpty()) null else result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendMessage(
        prompt: String, 
        currentSummary: String? = null,
        nearbyPlaces: String? = null,
        symptomCache: String? = null
    ): GenerateContentResponse {
        val userContent = content(role = Constants.ROLE_USER) { text(prompt) }
        _history.add(userContent)

        val requestList = mutableListOf<Content>()
        
        nearbyPlaces?.let {
            requestList.add(content(role = Constants.ROLE_USER) { 
                text(BuildConfig.CONTEXT_LOCATION.format(it)) 
            })
            requestList.add(content(role = Constants.ROLE_MODEL) { 
                text("Tôi đã ghi nhận các địa điểm y tế gần bạn.") 
            })
        }

        symptomCache?.let {
            requestList.add(content(role = Constants.ROLE_USER) {
                text(BuildConfig.CONTEXT_SYMPTOMS.format(it))
            })
            requestList.add(content(role = Constants.ROLE_MODEL) {
                text("Đã ghi nhớ các triệu chứng đã biết. Tôi sẽ không hỏi lặp lại.")
            })
        }

        currentSummary?.let { 
            requestList.add(content(role = Constants.ROLE_USER) { 
                text(BuildConfig.CONTEXT_SUMMARY.format(it))
            })
            requestList.add(content(role = Constants.ROLE_MODEL) { 
                text("Đã hiểu bệnh sử.") 
            })
        }

        val maxRecent = if (currentSummary != null || symptomCache != null) 6 else 12
        requestList.addAll(_history.takeLast(maxRecent))

        try {
            val response = model.generateContent(requestList)
            response.text?.let { responseText ->
                _history.add(content(role = Constants.ROLE_MODEL) { text(responseText) })
            }
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error in sendMessage", e)
            throw e
        }
    }
}
