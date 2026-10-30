package com.lzy.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lzy.myapplication.data.ChallengeType
import com.lzy.myapplication.data.PiggyBank
import com.lzy.myapplication.ui.components.PiggyBankCard
import com.lzy.myapplication.ui.theme.*
import com.lzy.myapplication.viewmodel.ExpenseViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiggyBankScreen(
    viewModel: ExpenseViewModel = viewModel(),
    onPiggyBankClick: (PiggyBank) -> Unit = {}
) {
    val piggyBanks by viewModel.allPiggyBanks.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐷 ", fontSize = 22.sp)
                        Text(
                            "小豬存錢罐",
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
                onClick = { showCreateDialog = true },
                containerColor = SoftMint,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "新增存錢罐")
            }
        },
        containerColor = Color(0xFFFFFBFF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 總儲蓄概覽
            item {
                val totalSaved = piggyBanks.sumOf { it.currentAmount }
                val totalTarget = piggyBanks.sumOf { it.targetAmount }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftMint.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐷", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "總儲蓄進度",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            "${String.format("%.0f", totalSaved)} / ${String.format("%.0f", totalTarget)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftMintDark
                        )
                        if (totalTarget > 0) {
                            val overallProgress = (totalSaved / totalTarget).coerceIn(0.0, 1.0)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { overallProgress.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = SoftMint,
                                trackColor = Color.White.copy(alpha = 0.6f),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${(overallProgress * 100).toInt()}%",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 快速模板
            item {
                Text(
                    text = "快速創建",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChallengeTemplateCard(
                        emoji = "📅",
                        title = "365存錢法",
                        subtitle = "每天遞增存入",
                        color = SoftMint,
                        modifier = Modifier.weight(1f),
                        onClick = { showCreateDialog = true }
                    )
                    ChallengeTemplateCard(
                        emoji = "📆",
                        title = "52週挑戰",
                        subtitle = "每週遞增存入",
                        color = BabyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { showCreateDialog = true }
                    )
                }
            }

            // 標題
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "我的存錢罐 (${piggyBanks.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // 空狀態
            if (piggyBanks.isEmpty()) {
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
                                "還沒有存錢罐哦～",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                            Text(
                                "點擊右下角 + 或上方模板開始吧！",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }
                    }
                }
            }

            // 存錢罐列表
            items(piggyBanks, key = { it.id }) { piggyBank ->
                PiggyBankCard(
                    piggyBank = piggyBank,
                    onClick = { onPiggyBankClick(piggyBank) }
                )
            }

            // 底部空間
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // 新增存錢罐對話框
    if (showCreateDialog) {
        CreatePiggyBankDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, emoji, target, targetDate, challenge, fixedAmt ->
                val colors = listOf(0xFFFFB5BA, 0xFFB5EAD7, 0xFFA0D2F0, 0xFFFFF5BA, 0xFFC3B1E1, 0xFFFF8C94)
                val color = colors[piggyBanks.size % colors.size]
                viewModel.addPiggyBank(name, emoji, target, color, targetDate, challenge, fixedAmt)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun ChallengeTemplateCard(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePiggyBankDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, emoji: String, target: Double, targetDate: Long?, challenge: ChallengeType, fixedAmount: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🐷") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedChallenge by remember { mutableStateOf(ChallengeType.NONE) }
    var targetDateText by remember { mutableStateOf("") }

    val emojiOptions = listOf("🐷", "📱", "✈️", "🏠", "🎮", "💍", "🚗", "🎓", "💰", "🎁")
    val challengeOptions = listOf(
        ChallengeType.NONE to "無挑戰",
        ChallengeType.DAY_365 to "365存錢法（每天遞增）",
        ChallengeType.WEEK_52 to "52週挑戰（每週遞增）",
        ChallengeType.FIXED_DAILY to "每日固定金額",
        ChallengeType.FIXED_WEEKLY to "每週固定金額"
    )

    var challengeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增存錢罐 🐷") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 名稱
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("名稱（如：iPhone 基金）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftMint,
                        cursorColor = SoftMint
                    )
                )

                // Emoji 選擇
                Text("選擇圖標", fontSize = 14.sp, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    emojiOptions.forEach { e ->
                        FilterChip(
                            selected = emoji == e,
                            onClick = { emoji = e },
                            label = { Text(e, fontSize = 18.sp) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftMint.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                // 目標金額
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("目標金額") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftMint,
                        cursorColor = SoftMint
                    )
                )

                // 挑戰模式
                Text("儲蓄挑戰（可選）", fontSize = 14.sp, color = TextSecondary)
                ExposedDropdownMenuBox(
                    expanded = challengeExpanded,
                    onExpandedChange = { challengeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = challengeOptions.find { it.first == selectedChallenge }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = challengeExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = challengeExpanded,
                        onDismissRequest = { challengeExpanded = false }
                    ) {
                        challengeOptions.forEach { (type, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedChallenge = type
                                    challengeExpanded = false
                                }
                            )
                        }
                    }
                }

                // 固定金額（挑戰用）
                if (selectedChallenge == ChallengeType.FIXED_DAILY || selectedChallenge == ChallengeType.FIXED_WEEKLY) {
                    OutlinedTextField(
                        value = targetDateText,
                        onValueChange = { targetDateText = it },
                        label = {
                            Text(
                                if (selectedChallenge == ChallengeType.FIXED_DAILY) "每日存入金額"
                                else "每週存入金額"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftMint,
                            cursorColor = SoftMint
                        )
                    )
                }

                // 目標日期（可選）
                OutlinedTextField(
                    value = targetDateText,
                    onValueChange = { targetDateText = it },
                    label = { Text("目標日期（可選，選填）") },
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
                    val target = targetAmount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && target > 0) {
                        val fixedAmt = if (selectedChallenge == ChallengeType.FIXED_DAILY || selectedChallenge == ChallengeType.FIXED_WEEKLY) {
                            targetDateText.toDoubleOrNull() ?: 0.0
                        } else 0.0
                        onCreate(name.trim(), emoji, target, null, selectedChallenge, fixedAmt)
                    }
                },
                enabled = name.isNotBlank() && (targetAmount.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("新增", color = SoftMint)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = TextSecondary)
            }
        }
    )
}
