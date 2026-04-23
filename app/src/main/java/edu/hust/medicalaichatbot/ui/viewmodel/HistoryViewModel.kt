package edu.hust.medicalaichatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.usecase.chat.DeleteThreadUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.GetMessagesUseCase
import edu.hust.medicalaichatbot.domain.usecase.chat.GetThreadsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getThreadsUseCase: GetThreadsUseCase,
    private val deleteThreadUseCase: DeleteThreadUseCase,
    private val getMessagesUseCase: GetMessagesUseCase
) : ViewModel() {

    private val _userId = MutableStateFlow<String>("guest")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val threads: StateFlow<List<ChatThread>> = _userId
        .flatMapLatest { userId -> getThreadsUseCase(userId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun deleteThread(threadId: String) {
        viewModelScope.launch {
            deleteThreadUseCase(threadId)
        }
    }

    fun getMessagesForThread(threadId: String): Flow<PagingData<ChatMessage>> {
        return getMessagesUseCase(threadId).cachedIn(viewModelScope)
    }

    class Factory(
        private val getThreadsUseCase: GetThreadsUseCase,
        private val deleteThreadUseCase: DeleteThreadUseCase,
        private val getMessagesUseCase: GetMessagesUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(getThreadsUseCase, deleteThreadUseCase, getMessagesUseCase) as T
        }
    }
}
