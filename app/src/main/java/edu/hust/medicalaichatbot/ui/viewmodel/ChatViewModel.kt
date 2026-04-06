package edu.hust.medicalaichatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.usecase.chat.GetMessagesUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.SendMessageUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _currentThreadId = MutableStateFlow<String?>(null)
    val currentThreadId = _currentThreadId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val messages: Flow<PagingData<ChatMessage>> = _currentThreadId
        .filterNotNull()
        .flatMapLatest { threadId ->
            getMessagesUseCase(threadId)
        }
        .cachedIn(viewModelScope)

    fun setCurrentThread(threadId: String) {
        _currentThreadId.value = threadId
    }

    fun sendMessage(text: String) {
        val threadId = _currentThreadId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            sendMessageUseCase(threadId, text)
            _isLoading.value = false
        }
    }

    class Factory(
        private val getMessagesUseCase: GetMessagesUseCase,
        private val sendMessageUseCase: SendMessageUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(getMessagesUseCase, sendMessageUseCase) as T
        }
    }
}
