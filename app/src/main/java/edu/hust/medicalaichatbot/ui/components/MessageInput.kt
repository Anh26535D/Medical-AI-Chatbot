package edu.hust.medicalaichatbot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SurfaceGray

private val SYMPTOM_CHIPS = listOf(
    "Sốt",
    "Ho",
    "Đau đầu",
    "Buồn nôn",
    "Đau bụng",
    "Khó thở",
    "Mệt mỏi",
    "Chóng mặt",
    "Đau ngực",
    "Mất ngủ"
)

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    prefillText: String = "",
    onPrefillConsumed: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val selectedChips = remember { mutableStateListOf<String>() }

    // Handle external prefill (from quick reply buttons)
    LaunchedEffect(prefillText) {
        if (prefillText.isNotEmpty()) {
            text = prefillText
            selectedChips.clear()
            onPrefillConsumed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Symptom Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SYMPTOM_CHIPS.forEach { symptom ->
                val isSelected = symptom in selectedChips
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedChips.remove(symptom)
                        } else {
                            selectedChips.add(symptom)
                        }
                        // Build text from selected chips
                        text = if (selectedChips.isNotEmpty()) {
                            "Tôi bị ${selectedChips.joinToString(", ").lowercase()}"
                        } else {
                            ""
                        }
                    },
                    label = {
                        Text(
                            text = symptom,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceGray,
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                        labelColor = Color.DarkGray,
                        selectedLabelColor = PrimaryBlue
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.Transparent,
                        selectedBorderColor = PrimaryBlue.copy(alpha = 0.5f),
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceGray
            ) {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        // Clear chip selection when user types manually
                        if (selectedChips.isNotEmpty() && !it.startsWith("Tôi bị")) {
                            selectedChips.clear()
                        }
                    },
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
                        selectedChips.clear()
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
