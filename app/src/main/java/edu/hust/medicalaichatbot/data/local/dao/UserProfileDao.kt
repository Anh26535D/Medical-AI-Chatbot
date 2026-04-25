package edu.hust.medicalaichatbot.data.local.dao

import androidx.room.*
import edu.hust.medicalaichatbot.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId ORDER BY isPrimary DESC, id ASC")
    fun getAllProfiles(userId: String): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)
    
    @Query("UPDATE user_profiles SET userId = :newUserId WHERE userId = :oldUserId")
    suspend fun migrateProfiles(oldUserId: String, newUserId: String)

    @Delete
    suspend fun deleteProfile(profile: UserProfileEntity)
    
    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)
}
