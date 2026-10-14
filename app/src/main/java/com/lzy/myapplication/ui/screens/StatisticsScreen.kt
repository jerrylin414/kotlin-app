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
import com.lzy.myapplication.data.Expense
import com.lzy.myapplication.data.ExpenseCategory
import com.lzy.myapplication.ui.components.AnimalAvatar
import com.lzy.myapplication.ui.theme.*

data class CategorySummary(
    val category: ExpenseCategory,
    val total: Double,
    val percentage: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    expenses: List<Expense>,
    totalExpense: Double
) {
    val expenseList = expenses.filter { !it.isIncome }

    // 按分類統計
    val summary = remember(expenseList) {
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
                        percentage = (total / totalExpense).toFloat().coerceIn(0f, 1f)
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
            // 總支出卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Coral.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💸", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "本月總支出",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            String.format("%.2f", totalExpense),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Coral
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
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
                                "還沒有支出記錄哦～",
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
