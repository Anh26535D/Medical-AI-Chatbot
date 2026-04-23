package edu.hust.medicalaichatbot.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "medical_ai_chatbot_prefs"
        private const val KEY_FIRST_TIME = "is_first_time"
        private const val KEY_LAST_VISIT = "last_visit_time"
        private const val RE_SHOW_ONBOARDING_DAYS = 30 // Show again after 30 days of inactivity
    }

    private var shouldShowOnboardingCached: Boolean? = null

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_FIRST_TIME, isFirstTime) }
    }

    fun shouldShowOnboarding(): Boolean {
        if (shouldShowOnboardingCached != null) return shouldShowOnboardingCached!!

        val isFirstTime = sharedPreferences.getBoolean(KEY_FIRST_TIME, true)
        if (isFirstTime) {
            shouldShowOnboardingCached = true
            return true
        }

        val lastVisit = sharedPreferences.getLong(KEY_LAST_VISIT, 0L)
        val currentTime = System.currentTimeMillis()
        val daysSinceLastVisit = (currentTime - lastVisit) / (1000 * 60 * 60 * 24)
        
        val result = daysSinceLastVisit >= RE_SHOW_ONBOARDING_DAYS
        shouldShowOnboardingCached = result
        return result
    }

    fun updateLastVisit() {
        sharedPreferences.edit { putLong(KEY_LAST_VISIT, System.currentTimeMillis()) }
    }
}
