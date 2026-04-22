package edu.hust.medicalaichatbot.utils

import edu.hust.medicalaichatbot.domain.model.TriageTag

data class ParsedLlmResponse(
    val thought: String? = null,
    val diagnosisGuess: String? = null,
    val symptomsObserved: List<String> = emptyList(),
    val message: String = "",
    val triageTag: TriageTag? = null,
    val extractedQuestions: List<String> = emptyList()
)

object LlmParser {
    fun parse(text: String): ParsedLlmResponse {
        val thought = extractTag(text, "thought")
        val diagnosisGuess = extractTag(text, "diagnosis_guess")
        val symptomsObserved = extractTag(text, "symptoms_observed")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() } ?: emptyList()
        val rawMessage = extractTag(text, "message") ?: text // Fallback to full text if no message tag
        val triageTagString = extractTag(text, "triage_tag")?.uppercase()
        
        val triageTag = try {
            if (triageTagString != null && triageTagString != "NONE") {
                TriageTag.valueOf(triageTagString)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            null
        }

        val (cleanMessage, extractedQuestions) = extractQuestionsAndCleanMessage(rawMessage)

        return ParsedLlmResponse(
            thought = thought,
            diagnosisGuess = diagnosisGuess,
            symptomsObserved = symptomsObserved,
            message = cleanMessage,
            triageTag = triageTag,
            extractedQuestions = extractedQuestions
        )
    }

    /**
     * Tách các dòng câu hỏi khỏi nội dung chính để hiển thị thành các nút gợi ý,
     * đồng thời loại bỏ các dòng đó khỏi nội dung chat bubble để tránh lặp lại.
     */
    private fun extractQuestionsAndCleanMessage(messageText: String): Pair<String, List<String>> {
        val questions = mutableListOf<String>()
        val remainingLines = mutableListOf<String>()
        
        val lines = messageText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if ((trimmed.startsWith("*") || trimmed.startsWith("-")) && trimmed.endsWith("?")) {
                var q = trimmed.removePrefix("*").removePrefix("-").trim()
                q = q.replace("**", "") // Xóa bold markdown
                if (q.length > 5) {
                    questions.add(q)
                }
            } else {
                // Nếu không phải là câu hỏi list gạch đầu dòng, giữ lại ở chat bubble
                // Loại bỏ cả các câu gợi ý bằng chữ "Hay bạn đang muốn nói điều gì khác?" nếu nó cũng là list
                if ((trimmed.startsWith("*") || trimmed.startsWith("-")) && trimmed.contains("điều gì khác")) {
                     var q = trimmed.removePrefix("*").removePrefix("-").trim()
                     q = q.replace("**", "")
                     questions.add(q)
                } else {
                     remainingLines.add(line)
                }
            }
        }

        // Nếu không tách được list gạch đầu dòng nào, thử quét câu hỏi độc lập cuối cùng
        if (questions.isEmpty()) {
            val lastLines = remainingLines.takeLast(2)
            val standaloneQ = lastLines.find { it.trim().endsWith("?") && it.length > 10 }
            if (standaloneQ != null) {
                var q = standaloneQ.trim().replace("**", "")
                questions.add(q)
                // Tuỳ chọn: Có thể xoá standaloneQ khỏi remainingLines tại đây, 
                // nhưng để an toàn thì giữ lại vì nó nằm trong văn xuôi.
            }
        }
        
        // Dọn dẹp khoảng trắng dư thừa
        var cleanMessage = remainingLines.joinToString("\n").trim()
        
        return Pair(cleanMessage, questions.take(5))
    }

    private fun extractTag(text: String, tagName: String): String? {
        val regex = Regex("<$tagName>(.*?)</$tagName>", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(text)
        if (match != null) {
            return match.groupValues[1].trim()
        }
        
        // Fallback for unclosed tags (common in streaming or truncated responses)
        val openTag = "<$tagName>"
        if (text.contains(openTag)) {
            val startIndex = text.indexOf(openTag) + openTag.length
            val content = text.substring(startIndex).trim()
            // If there's another tag starting later, cut it off
            val nextTagIndex = content.indexOf("<")
            return if (nextTagIndex != -1) {
                content.substring(0, nextTagIndex).trim()
            } else {
                content
            }
        }
        
        return null
    }
}
