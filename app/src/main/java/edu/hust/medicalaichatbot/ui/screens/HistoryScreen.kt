package edu.hust.medicalaichatbot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.hust.medicalaichatbot.R
import edu.hust.medicalaichatbot.data.model.HealthStatus
import edu.hust.medicalaichatbot.domain.model.ChatThread
import edu.hust.medicalaichatbot.ui.theme.BackgroundGray
import edu.hust.medicalaichatbot.ui.theme.EmergencyRed
import edu.hust.medicalaichatbot.ui.theme.PrimaryBlue
import edu.hust.medicalaichatbot.ui.theme.SuccessGreen
import edu.hust.medicalaichatbot.ui.theme.TextGray
import edu.hust.medicalaichatbot.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onThreadClick: (String) -> Unit = {}
) {
    val threads by viewModel.threads.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredThreads = remember(threads, searchQuery) {
        if (searchQuery.isBlank()) threads
        else threads.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.summary?.contains(searchQuery, ignoreCase = true) == true 
        }
    }

    Scaffold(
        topBar = { HistoryTopBar() },
        bottomBar = { HistoryBottomNavigation(onHomeClick, onProfileClick, onHelpClick) },
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
            
            if (filteredThreads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Chưa có lịch sử tư vấn", color = TextGray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredThreads) { thread ->
                        HistoryCard(thread) { onThreadClick(thread.id) }
                    }
                }
            }
        }
    }
}

private fun Modifier.size(size: Int): Modifier = this.size(size.dp)

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
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(8.dp),
                color = PrimaryBlue
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.history_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.search_hint), color = TextGray, fontSize = 14.sp) },
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
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryCard(thread: ChatThread, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.forLanguageTag("vi"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    val dateDisplay = if (java.text.DateFormat.getDateInstance().format(Date(thread.lastUpdated)) == java.text.DateFormat.getDateInstance().format(Date())) {
        "HÔM NAY, ${timeFormat.format(Date(thread.lastUpdated))}"
    } else {
        "${dateFormat.format(Date(thread.lastUpdated)).uppercase(Locale.getDefault())}"
    }

    val triageLevel = remember(thread.summary) {
        when {
            thread.summary?.contains("[TRIAGE: RED]", ignoreCase = true) == true -> HealthStatus.CRITICAL
            thread.summary?.contains("[TRIAGE: ORANGE]", ignoreCase = true) == true -> HealthStatus.MONITORING
            thread.summary?.contains("[TRIAGE: YELLOW]", ignoreCase = true) == true -> HealthStatus.MONITORING
            else -> HealthStatus.STABLE
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateDisplay,
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = thread.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                StatusBadge(triageLevel)
            }
            
            if (!thread.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                val cleanSummary = thread.summary.replace(Regex("\\[TRIAGE:.*?\\]"), "").trim()
                Text(
                    text = cleanSummary,
                    fontSize = 13.sp,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Model: ${thread.modelName.split("/").last()}",
                    fontSize = 10.sp,
                    color = TextGray.copy(alpha = 0.7f)
                )
                Text(
                    text = stringResource(R.string.view_details),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: HealthStatus) {
    val (backgroundColor, contentColor, textRes, icon) = when (status) {
        HealthStatus.STABLE -> Quad(SuccessGreen.copy(alpha = 0.2f), SuccessGreen, R.string.status_stable, Icons.Default.CheckCircle)
        HealthStatus.MONITORING -> Quad(Color(0xFFFFF3E0), Color(0xFFE65100), R.string.status_monitoring, Icons.Default.Warning)
        HealthStatus.CRITICAL -> Quad(EmergencyRed.copy(alpha = 0.1f), EmergencyRed, R.string.status_monitoring, Icons.Default.Error) // Reuse monitoring string or add critical
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
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
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = stringResource(textRes),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun HistoryBottomNavigation(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(80.dp)
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.Home,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.nav_home),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                selected = false,
                onClick = onHomeClick,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.nav_history),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.nav_profile),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                selected = false,
                onClick = onProfileClick,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.nav_help),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                selected = false,
                onClick = onHelpClick,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
        }
    }
}
