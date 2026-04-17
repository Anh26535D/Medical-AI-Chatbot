package edu.hust.medicalaichatbot.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.domain.model.ChatMessage
import edu.hust.medicalaichatbot.domain.model.MessageRole
import edu.hust.medicalaichatbot.ui.theme.*
import edu.hust.medicalaichatbot.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel = viewModel(),
    onHistoryClick: () -> Unit = {}
) {
    val messages = chatViewModel.messages.collectAsLazyPagingItems()
    val isLoading by chatViewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        if (chatViewModel.currentThreadId.value == null) {
            chatViewModel.setCurrentThread("default_thread")
        }
    }
    
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomNavigation(onHistoryClick) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatSection(
                messages = messages,
                isLoading = isLoading,
                modifier = Modifier.weight(1f)
            )
            MessageInput(onSendMessage = { chatViewModel.sendMessage(it) })
        }
    }
}

@Composable
fun HomeTopBar() {
    Surface(
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Trợ lý sức khỏe AI",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = SurfaceGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = PrimaryBlue)
                }
            }
        }
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
                if (message.role == MessageRole.ASSISTANT || message.role == MessageRole.ERROR) {
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
    // Tách Tag Triage nếu có
    val triageTag = remember(text) {
        when {
            text.contains("[TRIAGE: RED]") -> "RED"
            text.contains("[TRIAGE: ORANGE]") -> "ORANGE"
            text.contains("[TRIAGE: YELLOW]") -> "YELLOW"
            text.contains("[TRIAGE: GREEN]") -> "GREEN"
            else -> null
        }
    }
    val cleanText = text.replace(Regex("\\[TRIAGE:.*?\\]"), "").trim()

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
                    Text(
                        text = cleanText,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.Black
                    )

                    if (triageTag != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TriageActionButtons(triageTag)
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
fun TriageActionButtons(tag: String) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (tag) {
            "RED" -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:115"))
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
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=bệnh viện gần nhất"))
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
            "ORANGE" -> {
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
            "YELLOW" -> {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=nhà thuốc gần nhất"))
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

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceGray
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Mô tả triệu chứng của bạn...", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    maxLines = 5
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(48.dp),
                enabled = text.isNotBlank()
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (text.isNotBlank()) PrimaryBlue else Color.LightGray,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(onHistoryClick: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.ChatBubble, null) },
            label = { Text("Tư vấn") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, null) },
            label = { Text("Bệnh án") },
            selected = false,
            onClick = onHistoryClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Cài đặt") },
            selected = false,
            onClick = {}
        )
    }
}
