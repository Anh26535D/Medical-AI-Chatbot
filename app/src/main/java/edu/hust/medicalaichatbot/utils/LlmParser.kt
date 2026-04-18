package edu.hust.medicalaichatbot.utils

import edu.hust.medicalaichatbot.domain.model.TriageTag

data class ParsedLlmResponse(
    val thought: String? = null,
    val diagnosisGuess: String? = null,
    val symptomsObserved: List<String> = emptyList(),
    val message: String = "",
    val triageTag: TriageTag? = null
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

        return ParsedLlmResponse(
            thought = thought,
            diagnosisGuess = diagnosisGuess,
            symptomsObserved = symptomsObserved,
            message = message,
            triageTag = triageTag
        )
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
