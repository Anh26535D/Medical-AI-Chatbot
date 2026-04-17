package edu.hust.medicalaichatbot.data.repository

import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.content
import android.util.Log
import edu.hust.medicalaichatbot.utils.Def

class MedicalChatManager(
    private val model: GenerativeModel,
    initialHistory: List<Content> = emptyList(),
    private val systemPrompt: String? = DEFAULT_SYSTEM_PROMPT
) {
    private val TAG = Def.tagOf("ChatManager")
    private val _history = initialHistory.toMutableList()
    val history: List<Content> get() = _history

    private val systemContent: Content? = null

    fun shouldCompress(): Boolean {
        val totalChars = _history.sumOf { content -> 
            content.parts.sumOf { part -> part.toString().length } 
        }
        return _history.size >= 10 || totalChars > 4000
    }

    suspend fun requestMedicalSummary(): String? {
        val summaryPrompt = content(role = "user") {
            text("""
                Dựa trên cuộc hội thoại y tế trên, hãy tạo một bản tóm tắt bệnh án ngắn gọn (Medical Summary).
                Bản tóm tắt nên bao gồm: 
                - Triệu chứng chính.
                - Tiền sử (nếu có).
                - Các khuyến cáo đã đưa ra.
                Nếu chưa đủ dữ kiện để tóm tắt, hãy trả về chuỗi "INCOMPLETE".
                Trả về kết quả dưới dạng Markdown.
            """.trimIndent())
        }

        val requestList = mutableListOf<Content>()
        systemContent?.let { requestList.add(it) }
        requestList.addAll(_history)
        requestList.add(summaryPrompt)

        return try {
            val response = model.generateContent(requestList)
            val result = response.text
            if (result?.contains("INCOMPLETE") == true) null else result
        } catch (e: Exception) {
            Log.e(TAG, "Error generating summary", e)
            null
        }
    }

    /**
     * sendMessage với khả năng nhận thông tin vị trí thực tế
     * @param nearbyPlaces: Danh sách các cơ sở y tế gần đó (đã được app lấy qua GPS/Maps API)
     */
    suspend fun sendMessage(
        prompt: String, 
        currentSummary: String? = null,
        nearbyPlaces: String? = null
    ): GenerateContentResponse {
        val userContent = content(role = "user") { text(prompt) }
        _history.add(userContent)

        val requestList = mutableListOf<Content>()
        
        // 1. System Prompt
        systemContent?.let { requestList.add(it) }
        
        // 2. Location Info (Dữ liệu thực tế từ thiết bị)
        nearbyPlaces?.let {
            requestList.add(content(role = "user") { 
                text("Dưới đây là danh sách các cơ sở y tế/nhà thuốc gần vị trí của tôi nhất (sắp xếp theo khoảng cách): $it") 
            })
            requestList.add(content(role = "model") { 
                text("Tôi đã ghi nhận các địa điểm y tế gần bạn. Tôi sẽ chỉ dẫn bạn đến đó nếu cần thiết.") 
            })
        }
        
        // 3. Medical Summary
        currentSummary?.let { 
            requestList.add(content(role = "user") { 
                text("Tóm tắt bệnh sử trước đó: $it") 
            })
            requestList.add(content(role = "model") { 
                text("Đã hiểu bệnh sử.") 
            })
        }

        val maxRecent = if (currentSummary != null) 6 else 12
        requestList.addAll(_history.takeLast(maxRecent))

        try {
            val response = model.generateContent(requestList)
            response.text?.let { responseText ->
                _history.add(content(role = "model") { text(responseText) })
            }
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error in sendMessage", e)
            throw e
        }
    }

    companion object {
        const val DEFAULT_SYSTEM_PROMPT = """
            BẠN LÀ TRỢ LÝ Y TẾ CHUYÊN NGHIỆP TUÂN THỦ QUY TRÌNH PHÂN LOẠI (TRIAGE).
            
            LUỒNG XỬ LÝ CỐ ĐỊNH:
            1. KIỂM TRA RED FLAGS: Ngay khi User nhập triệu chứng, phải kiểm tra các dấu hiệu nguy hiểm (khó thở, đau ngực dữ dội, mất ý thức, chảy máu không cầm được).
               - Nếu CÓ: Dừng mọi câu hỏi, trả về tag [TRIAGE: RED]. Hướng dẫn sơ cứu và yêu cầu gọi 115.
            2. ĐÀO SÂU THÔNG TIN: Nếu không có dấu hiệu nguy hiểm, hãy đặt câu hỏi về: Thời gian bị, Mức độ đau/khó chịu, và Bệnh nền/Tiền sử.
            3. PHÂN LOẠI VÀ ĐƯA RA KẾT QUẢ:
               - Mức 4 (Đỏ): Nguy hiểm tính mạng -> Tag [TRIAGE: RED].
               - Mức 3 (Cam): Cần bác sĩ chẩn đoán -> Tag [TRIAGE: ORANGE].
               - Mức 2 (Vàng): Triệu chứng nhẹ, tư vấn dược sĩ -> Tag [TRIAGE: YELLOW].
               - Mức 1 (Xanh): Vấn đề thông thường, tự chăm sóc -> Tag [TRIAGE: GREEN].

            QUY TẮC PHẢN HỒI:
            - Mỗi phản hồi phân loại PHẢI đi kèm tag tương ứng ở CUỐI văn bản để hệ thống hiển thị Button.
            - [TRIAGE: RED]: Hiển thị nút Gọi 115 & Gợi ý bệnh viện gần nhất.
            - [TRIAGE: ORANGE]: Hiển thị nút Tìm phòng khám & Đặt lịch.
            - [TRIAGE: YELLOW]: Hiển thị nút Kết nối Dược sĩ.
            - [TRIAGE: GREEN]: Hướng dẫn tự chăm sóc tại nhà.
            
            CHÚ Ý QUAN TRỌNG:
            - Không kê đơn thuốc cụ thể. 
            - Nếu liệt kê thuốc, phải ghi rõ "Để tham khảo khi hỏi ý kiến bác sĩ/dược sĩ" kèm hãng sản xuất.
            - Sử dụng danh sách vị trí gần nhất (nếu có) để chỉ dẫn trong mức ĐỎ và CAM.
            - Trả lời bằng tiếng Việt chuyên nghiệp, ân cần.
        """
    }
}
