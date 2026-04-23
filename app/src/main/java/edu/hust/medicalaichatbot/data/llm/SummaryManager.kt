package edu.hust.medicalaichatbot.data.llm

import android.util.Log
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import edu.hust.medicalaichatbot.BuildConfig
import edu.hust.medicalaichatbot.data.llm.parser.SummaryResponse
import edu.hust.medicalaichatbot.data.llm.parser.SummaryResponseParser
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.utils.Constants
import edu.hust.medicalaichatbot.utils.Def

class SummaryManager(
    private val model: GenerativeModel
) {
    private val TAG = Def.tagOf("SummaryManager")

    suspend fun generateSummary(history: List<ChatMessage>, triageTag: String? = null): SummaryResponse? {
        val triageInfo = if (triageTag != null) "Triage Level: $triageTag\n" else ""
        
        val historyText = history.joinToString("\n") { 
            "${if (it.role == MessageRole.USER) "User" else "AI"}: ${it.content}"
        }

        val summaryPrompt = content(role = Constants.ROLE_USER) {
            text(String.format(BuildConfig.SUMMARY_SYSTEM_PROMPT, triageInfo) + "\n\nHistory:\n$historyText")
        }

        return try {
            val response = model.generateContent(listOf(summaryPrompt))
            val result = response.text
            if (result == null || result.contains("INCOMPLETE")) {
                null
            } else {
                SummaryResponseParser.parse(result)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating summary", e)
            null
        }
    }
}
