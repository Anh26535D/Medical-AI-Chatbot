package edu.hust.medicalaichatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import edu.hust.medicalaichatbot.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun updateProfile(age: String, birthYear: String, gender: String, conditions: List<String>) {
        _userProfile.update {
            it.copy(
                age = age,
                birthYear = birthYear,
                gender = gender,
                conditions = conditions,
                isInitial = false
            )
        }
    }
}
