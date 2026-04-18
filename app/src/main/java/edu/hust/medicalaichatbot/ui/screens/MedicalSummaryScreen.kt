package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.viewmodel.HistoryViewModel

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import org.json.JSONArray
import org.json.JSONException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSummaryScreen(
    threadId: String,
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit
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
                        Text(
                            text = "Bản tóm tắt bệnh án",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val displaySummary = thread.summary?.replace(Regex("\\[TRIAGE:.*?\\]"), "")?.trim()
                        
                        Text(
                            text = displaySummary ?: "Chưa có bản tóm tắt cho cuộc hội thoại này.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!thread.symptomCache.isNullOrBlank()) {
                    Text(
                        text = "Triệu chứng & Thông tin ghi nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val symptomsJson = thread.symptomCache
                    try {
                        val jsonArray = JSONArray(symptomsJson)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val q = obj.optString("q")
                            val a = obj.optString("a")
                            
                            SymptomItem(question = q, answer = a)
                            if (i < jsonArray.length() - 1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))
                            }
                        }
                    } catch (e: JSONException) {
                        Text(text = symptomsJson, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
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
