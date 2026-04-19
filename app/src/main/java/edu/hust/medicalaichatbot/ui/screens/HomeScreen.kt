package edu.hust.medicalaichatbot.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.domain.model.TriageTag
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import edu.hust.medicalaichatbot.utils.LlmParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel()
) {
    val messages = chatViewModel.messages.collectAsLazyPagingItems()
    val isLoading by chatViewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        if (chatViewModel.currentThreadId.value == null) {
            chatViewModel.setCurrentThread("default_thread")
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        ChatSection(
            messages = messages,
            isLoading = isLoading,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun ChatSection(
    messages: LazyPagingItems<ChatMessage>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.itemCount) {
        if (messages.itemCount > 0) {
            listState.animateScrollToItem(messages.itemCount - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                val dateString = SimpleDateFormat("dd MMMM", Locale.forLanguageTag("vi")).format(Date())
                Text(
                    text = stringResource(R.string.today_label, dateString),
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        items(
            count = messages.itemCount,
            key = messages.itemKey { it.id },
            contentType = messages.itemContentType { "chat_message" }
        ) { index ->
            val message = messages[index]
            if (message != null) {
                if (message.role == MessageRole.MODEL || message.role == MessageRole.ERROR) {
                    AiMessage(text = message.content, timestamp = message.timestamp)
                } else {
                    UserMessage(text = message.content, timestamp = message.timestamp)
                }
            }
        }

        if (isLoading) {
            item {
                AiLoadingIndicator()
            }
        }
    }
}

@Composable
fun AiMessage(text: String, timestamp: Long) {
    val parsedResponse = remember(text) { LlmParser.parse(text) }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(modifier = Modifier.fillMaxWidth().padding(end = 40.dp), horizontalArrangement = Arrangement.Start) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = PrimaryBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Surface(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Hiển thị phần Nhận định Sơ bộ và Triệu chứng nếu có
                    if (parsedResponse.diagnosisGuess != null || parsedResponse.symptomsObserved.isNotEmpty()) {
                        AiAssessmentSection(parsedResponse)
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SurfaceGray)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = parsedResponse.message,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.Black
                    )

                    parsedResponse.triageTag?.let { tag ->
                        Spacer(modifier = Modifier.height(12.dp))
                        TriageLevelBox(tag)
                        Spacer(modifier = Modifier.height(12.dp))
                        TriageActionButtons(tag)
                    }
                }
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun AiAssessmentSection(response: edu.hust.medicalaichatbot.utils.ParsedLlmResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MedicalServices, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bác sĩ AI nhận định",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        if (response.diagnosisGuess != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dự đoán: ${response.diagnosisGuess}",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        if (response.symptomsObserved.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(text = "Triệu chứng: ", fontSize = 12.sp, color = TextGray)
                Text(
                    text = response.symptomsObserved.joinToString(", "),
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun TriageLevelBox(tag: TriageTag) {
    val (color, title, description) = when (tag) {
        TriageTag.RED -> Triple(Color.Red, "CẤP CỨU NGAY (Mức 4)", "Triệu chứng nguy hiểm tính mạng. Cần can thiệp y tế ngay lập tức.")
        TriageTag.ORANGE -> Triple(Color(0xFFFFA500), "CẦN ĐI KHÁM (Mức 3)", "Triệu chứng cần bác sĩ chẩn đoán sớm để tránh diễn biến xấu.")
        TriageTag.YELLOW -> Triple(Color(0xFFFFD700), "HỎI DƯỢC SĨ (Mức 2)", "Triệu chứng nhẹ, có thể điều trị bằng thuốc không kê đơn dưới sự hướng dẫn.")
        TriageTag.GREEN -> Triple(SuccessGreen, "TỰ CHĂM SÓC (Mức 1)", "Tình trạng ổn định. Có thể tự theo dõi và chăm sóc tại nhà.")
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (tag == TriageTag.RED) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TriageActionButtons(tag: TriageTag) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (tag) {
            TriageTag.RED -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:115".toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Phone, null)
                    Spacer(Modifier.width(8.dp))
                    Text("GỌI CẤP CỨU 115", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=bệnh viện gần nhất".toUri())
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tìm bệnh viện gần đây")
                }
            }
            TriageTag.ORANGE -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = android.provider.CalendarContract.Events.CONTENT_URI
                            putExtra(android.provider.CalendarContract.Events.TITLE, "Khám bệnh (Hẹn từ AI Chatbot)")
                            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, "Tư vấn sức khỏe định kỳ")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Đặt lịch khám bác sĩ")
                }
            }
            TriageTag.YELLOW -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=nhà thuốc gần nhất".toUri())
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocalPharmacy, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tìm nhà thuốc gần nhất")
                }
            }
            TriageTag.GREEN -> {
                // No specific action button for GREEN yet, or can add "Health Tips"
            }
        }
    }
}

@Composable
fun UserMessage(text: String, timestamp: Long) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth().padding(start = 60.dp), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 0.dp),
                color = PrimaryBlue
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 15.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                )
            }
            Text(
                text = timeFormat.format(Date(timestamp)),
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun AiLoadingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
        Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = SurfaceGray) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(16.dp), tint = TextGray)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "AI đang phân tích triệu chứng...",
            fontSize = 13.sp,
            color = TextGray,
            modifier = Modifier
                .background(SurfaceGray, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

