package com.lzy.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lzy.myapplication.data.Expense
import com.lzy.myapplication.data.ExpenseCategory
import com.lzy.myapplication.ui.components.AnimalAvatar
import com.lzy.myapplication.ui.components.PeriodSelectorBar
import com.lzy.myapplication.ui.components.SavingsGoalsSection
import com.lzy.myapplication.ui.theme.*
import com.lzy.myapplication.viewmodel.ExpenseViewModel
import com.lzy.myapplication.viewmodel.FilterMode

data class CategorySummary(
    val category: ExpenseCategory,
    val total: Double,
    val percentage: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val filterMode by viewModel.filterMode.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val filteredExpense by viewModel.filteredExpense.collectAsState()
    val filteredIncome by viewModel.filteredIncome.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()

    // Category breakdown from filtered expenses
    val expenseList = filteredExpenses.filter { !it.isIncome }
    val totalExpenseForCalc = if (expenseList.sumOf { it.amount } > 0) expenseList.sumOf { it.amount } else 1.0

    val summary = remember(expenseList, totalExpenseForCalc) {
        if (expenseList.isEmpty()) {
            emptyList()
        } else {
            expenseList
                .groupBy { it.category }
                .map { (catName, items) ->
                    val category = try {
                        ExpenseCategory.valueOf(catName)
                    } catch (e: Exception) {
                        ExpenseCategory.OTHER
                    }
                    val total = items.sumOf { it.amount }
                    CategorySummary(
                        category = category,
                        total = total,
                        percentage = (total / totalExpenseForCalc).toFloat().coerceIn(0f, 1f)
                    )
                }
                .sortedByDescending { it.total }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📊 ", fontSize = 22.sp)
                        Text(
                            "小豬統計",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFFFFBFF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 時間篩選
            item {
                PeriodSelectorBar(
                    currentMode = filterMode,
                    selectedYear = selectedYear,
                    selectedMonth = selectedMonth,
                    onFilterChanged = { mode, year, month ->
                        viewModel.setFilterMode(mode, year, month)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 儲蓄目標
            item {
                SavingsGoalsSection(viewModel = viewModel)
            }

            // 收入/支出摘要
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftMint.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📥 收入", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                String.format("+%.2f", filteredIncome),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftMintDark
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Coral.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📤 支出", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                String.format("-%.2f", filteredExpense),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Coral
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 分類標題
            item {
                Text(
                    text = "📊 分類佔比",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // 空狀態
            if (summary.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "此時間範圍內沒有支出記錄哦～",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 分類統計列表
            items(summary) { item ->
                CategoryBar(
                    summary = item,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryBar(
    summary: CategorySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimalAvatar(
                emoji = summary.category.emoji,
                backgroundColor = summary.category.color.copy(alpha = 0.3f),
                size = 40.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        summary.category.label,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        String.format("%.2f", summary.total),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                // 進度條
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0F0F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(summary.percentage)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(summary.category.color)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f%%", summary.percentage * 100),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
