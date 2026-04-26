package edu.hust.medicalaichatbot.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MarkdownUtils {

    fun parseMarkdown(text: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { index, line ->
                parseLine(line)
                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.parseLine(line: String) {
        val trimmedLine = line.trim()
        
        when {
            // (### Heading)
            trimmedLine.startsWith("### ") -> {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                    parseInlines(trimmedLine.removePrefix("### ").trim())
                }
            }
            trimmedLine.startsWith("## ") -> {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    parseInlines(trimmedLine.removePrefix("## ").trim())
                }
            }
            trimmedLine.startsWith("# ") -> {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                    parseInlines(trimmedLine.removePrefix("# ").trim())
                }
            }
            // Bullet points
            trimmedLine.startsWith("* ") || trimmedLine.startsWith("- ") || trimmedLine.startsWith("•") -> {
                append("  • ")
                val content = if (trimmedLine.startsWith("•")) {
                    trimmedLine.removePrefix("•").trim()
                } else {
                    trimmedLine.substring(2).trim()
                }
                parseInlines(content)
            }
            else -> {
                parseInlines(line)
            }
        }
    }

    private fun AnnotatedString.Builder.parseInlines(text: String) {
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        val italicRegex = Regex("""\*(.*?)\*""")
        
        val matches = (boldRegex.findAll(text) + italicRegex.findAll(text))
            .sortedBy { it.range.first }
            .toList()

        var lastMatchEnd = 0
        for (match in matches) {
            if (match.range.first < lastMatchEnd) continue

            append(text.substring(lastMatchEnd, match.range.first))

            val isBold = match.value.startsWith("**")
            val content = match.groupValues[1]

            if (isBold) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(content)
                }
            } else {
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(content)
                }
            }
            lastMatchEnd = match.range.last + 1
        }

        if (lastMatchEnd < text.length) {
            append(text.substring(lastMatchEnd))
        }
    }
}
