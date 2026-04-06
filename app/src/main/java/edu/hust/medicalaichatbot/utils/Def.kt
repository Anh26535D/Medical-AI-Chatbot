package edu.hust.medicalaichatbot.utils

object Def {
    private const val BASE_TAG = "MedAI@"

    fun tagOf(name: String): String {
        return BASE_TAG + name
    }
}
