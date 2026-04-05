package edu.hust.medicalaichatbot.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.data.model.ChatMessage
import edu.hust.medicalaichatbot.data.model.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val TAG = "ChatViewModel"
        private const val MAX_INPUT_LENGTH = 1000
        private const val DEFAULT_MODEL_NAME = "gemini-2.5-flash-lite"
        private const val MODEL_NAME_KEY = "model_name"
    }

    private val _messages = MutableStateFlow(listOf(
        ChatMessage(application.getString(R.string.chat_welcome_msg), MessageRole.AI)
    ))
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        setupRemoteConfig()
    }

    private fun setupRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(MODEL_NAME_KEY to DEFAULT_MODEL_NAME))
        
        viewModelScope.launch {
            try {
                remoteConfig.fetchAndActivate().await()
            } catch (e: Exception) {
                Log.e(TAG, "Remote Config fetch failed: ${e.message}")
            }
        }
    }

    private fun getGenerativeModel() = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(remoteConfig.getString(MODEL_NAME_KEY))

    private var chat = getGenerativeModel().startChat(
        history = listOf(
            content(role = "user") { text("You are a helpful medical assistant. Provide concise and useful health advice. Always advise consulting a real doctor for serious symptoms.") },
            content(role = "model") { text("I understand. I will act as a helpful medical assistant.") }
        )
    )

    fun sendMessage(userText: String) {
        val sanitizedText = userText.trim()
        
        if (sanitizedText.isBlank()) return
        
        if (sanitizedText.length > MAX_INPUT_LENGTH) {
            _messages.value += ChatMessage(getApplication<Application>().getString(R.string.msg_too_long), MessageRole.AI)
            return
        }

        viewModelScope.launch {
            _messages.value += ChatMessage(sanitizedText, MessageRole.USER)
            _isLoading.value = true

            try {
                // Ensure we use the latest model name from Remote Config
                // Note: Re-initializing chat on every message might lose history if the model changes.
                // In a real app, you might check if the model name changed before re-initializing.

                val response = chat.sendMessage(sanitizedText)
                val aiResponseText = response.text ?: getApplication<Application>().getString(R.string.ai_error_process)
                _messages.value += ChatMessage(aiResponseText, MessageRole.AI)
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API Error: ${e.message}")
                _messages.value += ChatMessage(getApplication<Application>().getString(R.string.error_generic), MessageRole.AI)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
