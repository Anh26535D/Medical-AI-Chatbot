package edu.hust.medicalaichatbot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.hust.medicalaichatbot.data.local.dao.ChatDao
import edu.hust.medicalaichatbot.data.local.dao.UserDao
import edu.hust.medicalaichatbot.data.local.entity.ChatMessageEntity
import edu.hust.medicalaichatbot.data.local.entity.ChatThread
import edu.hust.medicalaichatbot.data.local.entity.User

@Database(
    entities = [User::class, ChatThread::class, ChatMessageEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "medical_ai_chatbot_db"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
