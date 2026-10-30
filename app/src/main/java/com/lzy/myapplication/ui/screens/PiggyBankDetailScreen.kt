package com.lzy.myapplication.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lzy.myapplication.data.ChallengeType
import com.lzy.myapplication.data.PiggyBank
import com.lzy.myapplication.data.PiggyBankTransaction
import com.lzy.myapplication.ui.theme.*
import com.lzy.myapplication.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiggyBankDetailScreen(
    piggyBankId: Long,
    viewModel: ExpenseViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val piggyBanks by viewModel.allPiggyBanks.collectAsState()
    val piggyBank = piggyBanks.find { it.id == piggyBankId }

    val transactionsFlow = remember(piggyBankId) {
        viewModel.getTransactionsForPiggyBank(piggyBankId)
    }
    val transactions by transactionsFlow.collectAsState(initial = emptyList())

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    if (piggyBank == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("找不到此存錢罐", color = TextSecondary)
        }
        return
    }

    val progress = if (piggyBank.targetAmount > 0) {
        (piggyBank.currentAmount / piggyBank.targetAmount).coerceIn(0.0, 1.5)
    } else 0.0
    val isComplete = piggyBank.currentAmount >= piggyBank.targetAmount
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat().coerceAtMost(1f),
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${piggyBank.emoji} ", fontSize = 22.sp)
                        Text(
                            piggyBank.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 存錢罐視覺化
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isComplete) Color(0xFFFFF8E1)
                        else Color(piggyBank.color).copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 大 Emoji
                        Text(
                            piggyBank.emoji,
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 目前 / 目標金額
                        Text(
                            String.format("%.0f", piggyBank.currentAmount),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isComplete) Color(0xFFF57F17) else TextPrimary
                        )
                        Text(
                            "/ ${String.format("%.0f", piggyBank.targetAmount)}",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 進度條
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp)),
                            color = if (isComplete) Color(0xFFFFD700) else SoftMint,
                            trackColor = Color.White.copy(alpha = 0.6f),
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${(progress * 100).toInt()}%",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        // 挑戰提示
                        val challengeType = try {
                            ChallengeType.valueOf(piggyBank.challengeType)
                        } catch (e: Exception) {
                            ChallengeType.NONE
                        }
                        if (challengeType != ChallengeType.NONE) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val challengeHint = when (challengeType) {
                                ChallengeType.DAY_365 -> "📅 365存錢法進行中"
                                ChallengeType.WEEK_52 -> "📆 52週挑戰進行中"
                                ChallengeType.FIXED_DAILY -> "💰 每日存 ${String.format("%.0f", piggyBank.fixedAmount)}"
                                ChallengeType.FIXED_WEEKLY -> "💰 每週存 ${String.format("%.0f", piggyBank.fixedAmount)}"
                                else -> ""
                            }
                            Text(
                                challengeHint,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        // 達標提示
                        if (isComplete) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "🎊 達標！恭喜～",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                        }
                    }
                }
            }

            // 快速存入按鈕
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "快速存入",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickAmounts = listOf(50.0, 100.0, 200.0, 500.0)
                    quickAmounts.forEach { amount ->
                        OutlinedButton(
                            onClick = {
                                amountInput = amount.toString()
                                noteInput = ""
                                showDepositDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SoftMint
                            )
                        ) {
                            Text(
                                "${String.format("%.0f", amount)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 存錢 / 提款按鈕
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            amountInput = ""
                            noteInput = ""
                            showDepositDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftMint,
                            contentColor = Color.White
                        )
                    ) {
                        Text("💰 存錢", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            amountInput = ""
                            noteInput = ""
                            showWithdrawDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Coral
                        )
                    ) {
                        Text("💸 提款", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 交易記錄標題
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📋 交易記錄",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "(${transactions.size} 筆)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 空狀態
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "還沒有交易記錄哦～\n點擊上方按鈕開始存錢吧！",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 交易列表
            items(transactions, key = { it.id }) { transaction ->
                TransactionItem(transaction = transaction)
            }

            // 底部空間
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // 存錢對話框
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("💰 存入 ${piggyBank.name}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("存入金額") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftMint,
                            cursorColor = SoftMint
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("備註（可選）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftMint,
                            cursorColor = SoftMint
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.deposit(piggyBank, amount, noteInput.ifBlank { null })
                            showDepositDialog = false
                        }
                    },
                    enabled = (amountInput.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    Text("存入", color = SoftMint)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    // 提款對話框
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("💸 從 ${piggyBank.name} 取出") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("取出金額") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Coral,
                            cursorColor = Coral
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("備註（可選）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Coral,
                            cursorColor = Coral
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && amount <= piggyBank.currentAmount) {
                            viewModel.withdraw(piggyBank, amount, noteInput.ifBlank { null })
                            showWithdrawDialog = false
                        }
                    },
                    enabled = (amountInput.toDoubleOrNull() ?: 0.0) > 0 &&
                            (amountInput.toDoubleOrNull() ?: 0.0) <= piggyBank.currentAmount
                ) {
                    Text("取出", color = Coral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun TransactionItem(transaction: PiggyBankTransaction) {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(transaction.date))
    val isDeposit = transaction.type == "DEPOSIT"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDeposit) SoftMint.copy(alpha = 0.2f)
                        else Coral.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isDeposit) "📥" else "📤", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isDeposit) "存入" else "取出",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextLight
                )
            }

            Text(
                text = if (isDeposit) "+${String.format("%.0f", transaction.amount)}"
                else "-${String.format("%.0f", transaction.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDeposit) SoftMintDark else Coral
            )
        }
    }
}
