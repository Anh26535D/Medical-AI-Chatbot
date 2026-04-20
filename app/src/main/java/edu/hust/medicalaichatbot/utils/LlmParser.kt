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
        val message = extractTag(text, "message") ?: text // Fallback to full text if no message tag
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

        val extractedQuestions = extractQuestions(message)

        return ParsedLlmResponse(
            thought = thought,
            diagnosisGuess = diagnosisGuess,
            symptomsObserved = symptomsObserved,
            message = message,
            triageTag = triageTag,
            extractedQuestions = extractedQuestions
        )
    }

    /**
     * Extract questions from AI message text.
     * Detects patterns like: * **Question text?** or - **Question text?**
     * Also detects standalone question lines ending with ?
     */
    fun extractQuestions(text: String): List<String> {
        val questions = mutableListOf<String>()
        
        // Pattern 1: Bold questions in bullet lists like "* **Question?**" or "- **Question?**"
        val boldQuestionRegex = Regex("""[*\-]\s*\*\*(.+?\?)\*\*""")
        boldQuestionRegex.findAll(text).forEach { match ->
            val q = match.groupValues[1].trim()
            if (q.length > 5) { // Filter very short matches
                questions.add(q)
            }
        }
        
        // If no bold questions found, try standalone question lines
        if (questions.isEmpty()) {
            text.lines()
                .map { it.trim() }
                .filter { it.endsWith("?") && it.length > 10 && !it.startsWith("Để") }
                .take(4)
                .forEach { questions.add(it) }
        }
        
        return questions.take(5) // Return max 5 questions
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
