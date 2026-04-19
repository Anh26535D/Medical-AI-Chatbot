package edu.hust.medicalaichatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.domain.usecase.chat.GetThreadsUseCase
import kotlinx.coroutines.flow.*

class HistoryViewModel(
    private val getThreadsUseCase: GetThreadsUseCase
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

    class Factory(
        private val getThreadsUseCase: GetThreadsUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(getThreadsUseCase) as T
        }
    }
}
