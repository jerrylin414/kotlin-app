package com.lzy.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lzy.myapplication.data.Expense
import com.lzy.myapplication.data.ExpenseCategory
import com.lzy.myapplication.ui.components.AnimalAvatar
import com.lzy.myapplication.ui.components.BalanceCard
import com.lzy.myapplication.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    expenses: List<Expense>,
    balance: Double,
    income: Double,
    expense: Double,
    onAddClick: () -> Unit,
    onDeleteExpense: (Expense) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐷 ", fontSize = 22.sp)
                        Text(
                            "小豬記賬",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Coral,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "記一筆")
            }
        },
        containerColor = Color(0xFFFFFBFF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 餘額卡片
            item {
                BalanceCard(
                    balance = balance,
                    income = income,
                    expense = expense
                )
            }

            // 分隔標題
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 收支明細",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${expenses.size} 筆)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 空狀態
            if (expenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐷", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "還沒有記錄哦～",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                            Text(
                                "點擊右下角 + 記第一筆吧！",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }
                    }
                }
            }

            // 記錄列表
            items(expenses, key = { it.id }) { expense ->
                ExpenseItem(
                    expense = expense,
                    onDelete = { onDeleteExpense(expense) }
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: () -> Unit
) {
    val category = try {
        ExpenseCategory.valueOf(expense.category)
    } catch (e: Exception) {
        ExpenseCategory.OTHER
    }

    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(expense.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
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
            // 動物頭像（分類圖標）
            AnimalAvatar(
                emoji = category.emoji,
                backgroundColor = category.color.copy(alpha = 0.3f),
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                if (expense.note.isNotBlank()) {
                    Text(
                        text = expense.note,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextLight
                )
            }

            // 金額
            Text(
                text = if (expense.isIncome) "+${expense.amount}" else "-${expense.amount}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (expense.isIncome) SoftMint else Coral
            )

            // 刪除按鈕
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "刪除",
                    tint = TextLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
