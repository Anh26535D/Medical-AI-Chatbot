package edu.hust.medicalaichatbot.data.repository

import edu.hust.medicalaichatbot.data.local.dao.UserProfileDao
import edu.hust.medicalaichatbot.data.local.entity.UserProfileEntity
import edu.hust.medicalaichatbot.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(private val userProfileDao: UserProfileDao) {
    
    fun getProfilesForUser(userId: String): Flow<List<UserProfile>> {
        return userProfileDao.getAllProfiles(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveProfile(userId: String, profile: UserProfile, id: Int = 0, isPrimary: Boolean = false) {
        val entity = UserProfileEntity(
            id = id,
            userId = userId,
            name = profile.name,
            birthYear = profile.birthYear,
            age = profile.age,
            gender = profile.gender,
            conditions = profile.conditions.joinToString(","),
            isPrimary = isPrimary
        )
        if (id == 0) {
            userProfileDao.insertProfile(entity)
        } else {
            userProfileDao.updateProfile(entity)
        }
    }

    suspend fun migrateGuestProfiles(newUserId: String) {
        userProfileDao.migrateProfiles("guest", newUserId)
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            birthYear = birthYear,
            age = age,
            gender = gender,
            conditions = if (conditions.isEmpty()) emptyList() else conditions.split(","),
            isInitial = birthYear.isEmpty() && age.isEmpty(),
            isPrimary = isPrimary
        )
    }
}
