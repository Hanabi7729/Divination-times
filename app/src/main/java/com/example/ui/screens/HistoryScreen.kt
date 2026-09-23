package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DivinationEntity
import com.example.viewmodel.OracleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: OracleViewModel,
    modifier: Modifier = Modifier
) {
    val strings by viewModel.strings.collectAsState()
    val allRecords by viewModel.allHistory.collectAsState()

    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Yes/No, 2: Choices, 3: Starred
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredRecords = allRecords.filter { record ->
        val matchesFilter = when (selectedFilter) {
            1 -> record.type == "YES_NO"
            2 -> record.type == "CHOICE"
            3 -> record.isFavorite
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            record.questionOrTitle.contains(searchQuery, ignoreCase = true) ||
                    record.answerOrChoice.contains(searchQuery, ignoreCase = true) ||
                    record.cosmicInsight.contains(searchQuery, ignoreCase = true)
        }
        matchesFilter && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Search Bar & Clear All button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = strings.historySearchHint,
                        color = Color(0xFF64748B),
                        fontSize = 13.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF818CF8)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("history_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF131533),
                    unfocusedContainerColor = Color(0xFF101229),
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF282B54),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            if (allRecords.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = strings.clearHistory,
                        tint = Color(0xFFF43F5E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips (All, Yes/No, Choices, Starred)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabChip(
                label = strings.filterAll,
                count = allRecords.size,
                selected = selectedFilter == 0,
                onClick = { selectedFilter = 0 }
            )
            FilterTabChip(
                label = strings.filterYesNo,
                count = allRecords.count { it.type == "YES_NO" },
                selected = selectedFilter == 1,
                onClick = { selectedFilter = 1 }
            )
            FilterTabChip(
                label = strings.filterChoice,
                count = allRecords.count { it.type == "CHOICE" },
                selected = selectedFilter == 2,
                onClick = { selectedFilter = 2 }
            )
            FilterTabChip(
                label = strings.filterFavorites,
                count = allRecords.count { it.isFavorite },
                selected = selectedFilter == 3,
                onClick = { selectedFilter = 3 }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Records List
        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.historyEmpty,
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("history_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRecords, key = { it.id }) { record ->
                    HistoryCard(
                        record = record,
                        strings = strings,
                        onToggleFav = { viewModel.toggleFavorite(record.id, record.isFavorite) },
                        onDelete = { viewModel.deleteHistoryRecord(record.id) },
                        onConsultAgain = {
                            if (record.type == "YES_NO") {
                                viewModel.setYesNoQuestion(record.questionOrTitle)
                                viewModel.setTab(0)
                            } else {
                                if (record.optionsJson.isNotBlank()) {
                                    val opts = record.optionsJson.split(",").map { it.trim() }
                                    viewModel.setChoicePreset(opts)
                                }
                                viewModel.setTab(1)
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Clear confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = strings.clearHistory, color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(text = strings.clearHistoryConfirm, color = Color(0xFFCBD5E1)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color(0xFFA5B4FC))
                }
            },
            containerColor = Color(0xFF131533),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun FilterTabChip(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF4F46E5) else Color(0xFF161838))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFFCBD5E1),
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($count)",
                    color = if (selected) Color(0xFFFDE047) else Color(0xFF64748B),
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

@Composable
private fun HistoryCard(
    record: DivinationEntity,
    strings: com.example.engine.LocalizedStrings,
    onToggleFav: () -> Unit,
    onDelete: () -> Unit,
    onConsultAgain: () -> Unit
) {
    val isYesNo = record.type == "YES_NO"
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(record.timestamp) { dateFormatter.format(Date(record.timestamp)) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF282B54), RoundedCornerShape(16.dp))
            .testTag("history_record_${record.id}"),
        color = Color(0xFF11132D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Type badge, Date, and Star/Delete icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (isYesNo) Color(0xFF4338CA) else Color(0xFF047857),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isYesNo) Icons.Default.HelpOutline else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isYesNo) "YES / NO" else "CHOICE",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  $formattedDate",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFav,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (record.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (record.isFavorite) Color(0xFFFDE047) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inquired Question or Options
            Text(
                text = record.questionOrTitle,
                color = Color.White,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (!isYesNo && record.optionsJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Options: ${record.optionsJson}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Oracle Result
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF181B3E))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Verdict: ",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = record.answerOrChoice,
                    color = Color(0xFFFDE047),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cosmic Insight
            Text(
                text = record.cosmicInsight,
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Tags and "Consult Again"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagChip(text = record.skinUsed, color = Color(0xFF818CF8))
                    TagChip(text = record.styleUsed, color = Color(0xFFF472B6))
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF202350))
                        .clickable { onConsultAgain() }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = strings.askAgain,
                        color = Color(0xFFA5B4FC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
