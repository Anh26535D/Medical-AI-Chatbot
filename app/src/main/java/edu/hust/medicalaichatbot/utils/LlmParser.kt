package edu.hust.medicalaichatbot.utils

data class ParsedLlmResponse(
    val thought: String? = null,
    val diagnosisGuess: String? = null,
    val symptomsObserved: List<String> = emptyList(),
    val message: String = "",
    val triageTag: String? = null
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
        val triageTag = extractTag(text, "triage_tag")?.uppercase()?.let {
            if (it == "NONE") null else it
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
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }
}
