package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.data.model.HealthStatus
import edu.hust.medicalaichatbot.data.model.MedicalHistory
import edu.hust.medicalaichatbot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(onBackClick: () -> Unit = {}, onHomeClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    
    val sampleHistory = listOf(
        MedicalHistory(
            id = "1",
            symptoms = "Đau đầu, chóng mặt",
            date = System.currentTimeMillis(),
            status = HealthStatus.STABLE,
            doctorName = "BS. Lê Văn Nam",
            doctorRole = stringResource(R.string.doctor_in_charge)
        ),
        MedicalHistory(
            id = "2",
            symptoms = "Đau thắt ngực",
            date = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
            status = HealthStatus.MONITORING,
            doctorName = "DS. Nguyễn Thị Lan",
            doctorRole = stringResource(R.string.pharmacist_consultant)
        ),
        MedicalHistory(
            id = "3",
            symptoms = "Đau đầu, chóng mặt",
            date = System.currentTimeMillis() - 86400000 * 60, // ~2 months ago
            status = HealthStatus.STABLE,
            doctorName = "BS. Lê Văn Nam",
            doctorRole = stringResource(R.string.doctor_in_charge)
        )
    )

    Scaffold(
        topBar = { HistoryTopBar() },
        bottomBar = { HistoryBottomNavigation(onHomeClick) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sampleHistory) { history ->
                    HistoryCard(history)
                }
            }
        }
    }
}

@Composable
fun HistoryTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = PrimaryBlue
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.app_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceGray
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(R.string.search_hint), color = TextGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = PrimaryBlue)
            }
        }
    }
}

@Composable
fun HistoryCard(history: MedicalHistory) {
    val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.forLanguageTag("vi"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    val dateDisplay = if (java.text.DateFormat.getDateInstance().format(Date(history.date)) == java.text.DateFormat.getDateInstance().format(Date())) {
        "HÔM NAY, ${timeFormat.format(Date(history.date))}"
    } else {
        "${dateFormat.format(Date(history.date)).uppercase(Locale.getDefault())}"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateDisplay,
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = history.symptoms,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                StatusBadge(history.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = SurfaceGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = history.doctorRole,
                            fontSize = 10.sp,
                            color = TextGray
                        )
                        Text(
                            text = history.doctorName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                
                Text(
                    text = stringResource(R.string.view_details),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: HealthStatus) {
    val (backgroundColor, contentColor, textRes, icon) = when (status) {
        HealthStatus.STABLE -> Quad(SuccessGreen, Color(0xFF2E7D32), R.string.status_stable, Icons.Default.CheckCircle)
        HealthStatus.MONITORING -> Quad(EmergencyRed, Color(0xFFC62828), R.string.status_monitoring, Icons.Default.Warning)
        HealthStatus.CRITICAL -> Quad(EmergencyRed, Color.Red, R.string.status_monitoring, Icons.Default.Error) // Reuse monitoring for simplicity
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = stringResource(textRes),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun HistoryBottomNavigation(onHomeClick: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_home)) },
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_history)) },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = LightBlue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_profile)) },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_help)) },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}
