package com.lzy.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lzy.myapplication.ui.theme.*
import com.lzy.myapplication.viewmodel.FilterMode
import java.util.*

@Composable
fun PeriodSelectorBar(
    currentMode: FilterMode,
    selectedYear: Int,
    selectedMonth: Int?,
    onFilterChanged: (FilterMode, Int, Int?) -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear + 1).toList().reversed()
    val monthNames = listOf(
        "1月", "2月", "3月", "4月", "5月", "6月",
        "7月", "8月", "9月", "10月", "11月", "12月"
    )

    var yearDropdownExpanded by remember { mutableStateOf(false) }
    var monthDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "📅 時間範圍",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Mode chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentMode == FilterMode.ALL_TIME,
                    onClick = { onFilterChanged(FilterMode.ALL_TIME, selectedYear, null) },
                    label = { Text("全部") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Lavender.copy(alpha = 0.2f),
                        selectedLabelColor = LavenderDark
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                FilterChip(
                    selected = currentMode == FilterMode.YEAR,
                    onClick = { onFilterChanged(FilterMode.YEAR, selectedYear, null) },
                    label = { Text("按年") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Lavender.copy(alpha = 0.2f),
                        selectedLabelColor = LavenderDark
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                FilterChip(
                    selected = currentMode == FilterMode.MONTH,
                    onClick = {
                        val month = selectedMonth ?: (Calendar.getInstance().get(Calendar.MONTH))
                        onFilterChanged(FilterMode.MONTH, selectedYear, month)
                    },
                    label = { Text("按月") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Lavender.copy(alpha = 0.2f),
                        selectedLabelColor = LavenderDark
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Year selector (visible for YEAR and MONTH modes)
            if (currentMode != FilterMode.ALL_TIME) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("年份：", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        FilterChip(
                            selected = false,
                            onClick = { yearDropdownExpanded = true },
                            label = { Text("${selectedYear}年") },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = yearDropdownExpanded,
                            onDismissRequest = { yearDropdownExpanded = false }
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text("${year}年") },
                                    onClick = {
                                        onFilterChanged(currentMode, year, selectedMonth)
                                        yearDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Month selector (only in MONTH mode)
                    if (currentMode == FilterMode.MONTH) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("月份：", fontSize = 14.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            FilterChip(
                                selected = false,
                                onClick = { monthDropdownExpanded = true },
                                label = {
                                    Text(
                                        monthNames[selectedMonth ?: 0]
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = monthDropdownExpanded,
                                onDismissRequest = { monthDropdownExpanded = false }
                            ) {
                                monthNames.forEachIndexed { index, name ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            onFilterChanged(FilterMode.MONTH, selectedYear, index)
                                            monthDropdownExpanded = false
                                        }
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
