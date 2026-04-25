package edu.hust.medicalaichatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import edu.hust.medicalaichatbot.data.repository.ProfileRepository
import edu.hust.medicalaichatbot.domain.model.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val userProfiles: StateFlow<List<UserProfile>> = authViewModel.authState
        .flatMapLatest { state ->
            val userId = when (state) {
                is AuthState.Success -> state.user.phoneNumber
                else -> "guest"
            }
            repository.getProfilesForUser(userId).onEach { profiles ->
                if (profiles.isEmpty()) {
                    ensurePrimaryProfileExists(userId)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun ensurePrimaryProfileExists(userId: String) {
        viewModelScope.launch {
            repository.saveProfile(
                userId = userId,
                profile = UserProfile(name = "Tôi"),
                id = 0,
                isPrimary = true
            )
        }
    }

    private fun getCurrentUserId(): String {
        return when (val state = authViewModel.authState.value) {
            is AuthState.Success -> state.user.phoneNumber
            else -> "guest"
        }
    }

    fun updateProfile(id: Int, name: String, birthYear: String, gender: String, conditions: List<String>) {
        val age = calculateAge(birthYear)
        val userId = getCurrentUserId()
        val currentProfile = userProfiles.value.find { it.id == id }
        val isPrimary = currentProfile?.isPrimary ?: false
        
        viewModelScope.launch {
            repository.saveProfile(
                userId = userId,
                profile = UserProfile(id, name, age, birthYear, gender, conditions, false, isPrimary),
                id = id,
                isPrimary = isPrimary
            )
        }
    }

    fun addProfile(name: String, birthYear: String, gender: String, conditions: List<String>) {
        val age = calculateAge(birthYear)
        val userId = getCurrentUserId()
        viewModelScope.launch {
            repository.saveProfile(
                userId = userId,
                profile = UserProfile(0, name, age, birthYear, gender, conditions, false),
                id = 0,
                isPrimary = false
            )
        }
    }

    private fun calculateAge(birthYear: String): String {
        return try {
            val year = birthYear.toInt()
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            (currentYear - year).toString()
        } catch (e: Exception) {
            ""
        }
    }

    class Factory(
        private val repository: ProfileRepository,
        private val authViewModel: AuthViewModel
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return ProfileViewModel(repository, authViewModel) as T
        }
    }
}
