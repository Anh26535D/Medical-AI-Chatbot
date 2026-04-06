package edu.hust.medicalaichatbot.data.model

enum class HealthStatus {
    STABLE, MONITORING, CRITICAL
}

data class MedicalHistory(
    val id: String,
    val symptoms: String,
    val date: Long,
    val status: HealthStatus,
    val doctorName: String,
    val doctorRole: String, // e.g., "Bác sĩ phụ trách", "Dược sĩ tư vấn"
    val doctorAvatarUrl: String? = null
)
