package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import edu.hust.medicalaichatbot.data.llm.parser.ChatResponseParser
import edu.hust.medicalaichatbot.data.llm.parser.SummaryResponseParser
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.model.TriageTag
import edu.hust.medicalaichatbot.ui.theme.EmergencyRed
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSummaryScreen(
    threadId: String,
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    onContinueChat: (String) -> Unit = {}
) {
    val threads by viewModel.threads.collectAsState()
    val thread = threads.find { it.id == threadId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tóm tắt y tế", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Implement share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PrimaryBlue,
                    navigationIconContentColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (thread == null) {
                Text("Không tìm thấy thông tin cuộc hội thoại.")
            } else {
                Text(
                    text = thread.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // ... existing analysis content ...
                        val parsedSummary = thread.summary?.let { SummaryResponseParser.parse(it) }
                        val diagnosisMatch = parsedSummary?.diagnosis
                        val triageLevel = parsedSummary?.triageLevel

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phân tích từ AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            
                            triageLevel?.let { level ->
                                val tag = try { TriageTag.valueOf(level.uppercase()) } catch (e: Exception) { null }
                                tag?.let { TriageSummaryBadge(it) }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (diagnosisMatch != null) {
                            Surface(
                                color = PrimaryBlue.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().border(1.dp, PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Kết luận dự kiến",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = diagnosisMatch,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        val rawSummaryText = parsedSummary?.mainSummary ?: thread.summary ?: ""
                        val displaySummary = rawSummaryText
                            .replace(Regex("<thought>.*?</thought>", RegexOption.DOT_MATCHES_ALL), "")
                            .replace(Regex("\\[TRIAGE:.*?\\]"), "")
                            .replace(Regex("(?i)\\*\\*Tóm tắt Bệnh án\\*\\*"), "")
                            .replace(Regex("Kết luận: .*?(\n|$)"), "")
                            .trim()
                        
                        if (displaySummary.isNotEmpty()) {
                            Text(
                                text = formatMarkdown(displaySummary),
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF374151)
                            )
                        } else {
                            Text(
                                text = "Chưa có bản tóm tắt cho cuộc hội thoại này.",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Surface(
                            onClick = { onContinueChat(threadId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryBlue
                        ) {
                            Text(
                                text = "Tiếp tục trò chuyện về bệnh án này",
                                modifier = Modifier.padding(vertical = 12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Lịch sử cuộc trò chuyện",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                val messages = viewModel.getMessagesForThread(threadId).collectAsLazyPagingItems()
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (messages.itemCount == 0) {
                            Text("Đang tải hội thoại...", fontSize = 14.sp, color = TextGray)
                        }
                        
                        for (i in 0 until messages.itemCount) {
                            val msg = messages[i]
                            if (msg != null && msg.role != MessageRole.ERROR) {
                                val cleanedContent = msg.content
                                    .replace(Regex("<thought>.*?</thought>", RegexOption.DOT_MATCHES_ALL), "")
                                    .trim()
                                
                                if (cleanedContent.isNotEmpty()) {
                                    ChatHistoryItem(
                                        role = msg.role,
                                        content = cleanedContent,
                                        timestamp = msg.timestamp
                                    )
                                    if (i < messages.itemCount - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = Color.LightGray.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TriageSummaryBadge(tag: TriageTag) {
    if (tag == TriageTag.NONE) return

    val (color, label) = when (tag) {
        TriageTag.RED -> EmergencyRed to "KHẨN CẤP"
        TriageTag.ORANGE -> Color(0xFFF59E0B) to "CẦN LƯU Ý"
        TriageTag.YELLOW -> Color(0xFFEAB308) to "THEO DÕI"
        TriageTag.GREEN -> SuccessGreen to "ỔN ĐỊNH"
        TriageTag.NONE -> return
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

fun formatMarkdown(text: String) = buildAnnotatedString {
    val lines = text.split("\n")
    lines.forEachIndexed { index, line ->
        val currentLine = line.trim()
        
        // Handle headers / bold text
        if (currentLine.startsWith("**") && currentLine.endsWith("**")) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                append(currentLine.removeSurrounding("**"))
            }
        } else if (currentLine.contains("**")) {
            val parts = currentLine.split("**")
            parts.forEachIndexed { pIndex, part ->
                if (pIndex % 2 == 1) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                        append(part)
                    }
                } else {
                    append(part)
                }
            }
        } else if (currentLine.startsWith("* ")) {
            append("  • ")
            append(currentLine.substring(2))
        } else {
            append(currentLine)
        }
        
        if (index < lines.size - 1) {
            append("\n")
        }
    }
}

@Composable
fun ChatHistoryItem(role: MessageRole, content: String, timestamp: Long) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val roleName = if (role == MessageRole.USER) "Bạn" else "Chatbot"
    val roleColor = if (role == MessageRole.USER) PrimaryBlue else Color(0xFF388E3C)
    
    var showSymptoms by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = roleName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor
                )
                
                if (role == MessageRole.MODEL) {
                    val parsed = remember(content) { ChatResponseParser.parse(content) }
                    if (parsed.symptomsObserved.isNotEmpty()) {
                        IconButton(
                            onClick = { showSymptoms = !showSymptoms },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Symptoms",
                                tint = if (showSymptoms) PrimaryBlue else TextGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 11.sp,
                color = TextGray
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))

        if (role == MessageRole.MODEL) {
            val parsed = remember(content) { ChatResponseParser.parse(content) }
            
            if (parsed.diagnosisGuess != null) {
                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Dự đoán: ${parsed.diagnosisGuess}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }

            if (showSymptoms && parsed.symptomsObserved.isNotEmpty()) {
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Triệu chứng ghi nhận:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = parsed.symptomsObserved.joinToString(", "),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Text(
                text = parsed.message,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        } else {
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SymptomItem(question: String, answer: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = answer,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
