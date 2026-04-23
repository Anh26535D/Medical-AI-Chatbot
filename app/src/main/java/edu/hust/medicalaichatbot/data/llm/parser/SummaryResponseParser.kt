package edu.hust.medicalaichatbot.data.llm.parser

data class SummaryResponse(
    val mainSummary: String = "",
    val diagnosis: String? = null,
    val triageLevel: String? = null,
    val rawText: String = ""
)

object SummaryResponseParser {
    fun parse(text: String): SummaryResponse {
        val diagnosis = extractTag(text, "diagnosis")
        val triageLevel = extractTag(text, "triage_level") ?: extractLegacyTriage(text)
        val summary = extractTag(text, "summary") ?: extractLegacySummary(text)

        return SummaryResponse(
            mainSummary = summary,
            diagnosis = diagnosis,
            triageLevel = triageLevel,
            rawText = text
        )
    }

    private fun extractLegacyTriage(text: String): String? {
        val triageRegex = Regex("\\[TRIAGE:\\s*(.*?)\\]", RegexOption.IGNORE_CASE)
        return triageRegex.find(text)?.groupValues?.get(1)?.trim()
    }

    private fun extractLegacySummary(text: String): String {
        return text.replace(Regex("<.*?>", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("\\[TRIAGE:.*?\\]", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    private fun extractTag(text: String, tagName: String): String? {
        val regex = Regex("<$tagName>(.*?)</$tagName>", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.trim()
    }
}
