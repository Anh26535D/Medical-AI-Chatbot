package edu.hust.medicalaichatbot.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
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
import edu.hust.medicalaichatbot.ui.theme.TextGray

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
    onPrefillConsumed: () -> Unit = {},
    showInitialChips: Boolean = false
) {
    var text by remember { mutableStateOf("") }
    val selectedChips = remember { mutableStateListOf<String>() }
    var showChips by remember { mutableStateOf(showInitialChips) }

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
        // Symptom Chips Row (Collapsible)
        AnimatedVisibility(
            visible = showChips,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SurfaceGray.copy(alpha = 0.7f),
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                            labelColor = Color.DarkGray,
                            selectedLabelColor = PrimaryBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            selectedBorderColor = PrimaryBlue.copy(alpha = 0.5f),
                            enabled = true,
                            selected = isSelected
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle Chips Button
            IconButton(
                onClick = { showChips = !showChips },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (showChips) Icons.Default.Bolt else Icons.Default.Add,
                    contentDescription = "Toggle symptoms",
                    tint = if (showChips) PrimaryBlue else TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceGray
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { newValue ->
                        text = newValue
                        // Clear chip selection when user types manually
                        if (selectedChips.isNotEmpty() && !newValue.startsWith("Tôi bị")) {
                            selectedChips.clear()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                "Mô tả triệu chứng...",
                                fontSize = 14.sp,
                                color = TextGray.copy(alpha = 0.7f)
                            )
                        }
                        innerTextField()
                    },
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                        selectedChips.clear()
                        showChips = false // Thu gọn sau khi gửi
                    }
                },
                modifier = Modifier.size(40.dp),
                enabled = text.isNotBlank()
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (text.isNotBlank()) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
