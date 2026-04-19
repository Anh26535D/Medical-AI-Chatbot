package edu.hust.medicalaichatbot.data.repository

import edu.hust.medicalaichatbot.data.local.dao.ChatDao
import edu.hust.medicalaichatbot.data.local.dao.UserDao
import edu.hust.medicalaichatbot.data.local.entity.User

class AuthRepository(
    private val userDao: UserDao,
    private val chatDao: ChatDao
) {
    suspend fun register(user: User): Result<Unit> {
        return try {
            val existingUser = userDao.getUserByPhone(user.phoneNumber)
            if (existingUser != null) {
                Result.failure(Exception("Phone number already registered"))
            } else {
                userDao.registerUser(user)
                // Migrate guest data to new user
                chatDao.migrateGuestData("guest", user.phoneNumber)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(phone: String, password: String): Result<User> {
        return try {
            val user = userDao.loginUser(phone, password)
            if (user != null) {
                // Migrate guest data to logged in user
                chatDao.migrateGuestData("guest", user.phoneNumber)
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid phone number or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
