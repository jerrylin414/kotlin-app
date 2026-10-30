package com.lzy.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.lzy.myapplication.data.GoalStatus
import com.lzy.myapplication.ui.theme.*
import com.lzy.myapplication.viewmodel.ExpenseViewModel
import com.lzy.myapplication.viewmodel.FilterMode
import java.util.*

@Composable
fun SavingsGoalsSection(
    viewModel: ExpenseViewModel
) {
    val goalProgress by viewModel.goalProgress.collectAsState()
    val filterMode by viewModel.filterMode.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    var showSetGoalDialog by remember { mutableStateOf(false) }
    var showReasonDialog by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("") }

    // Only show goal section in MONTH mode
    if (filterMode != FilterMode.MONTH) return

    val progress = goalProgress ?: return
    val statusColor = when (progress.status) {
        GoalStatus.GREEN -> AlertGreenLight
        GoalStatus.YELLOW -> AlertYellowLight
        GoalStatus.RED -> AlertRedLight
    }
    val statusIcon = when (progress.status) {
        GoalStatus.GREEN -> "🟢"
        GoalStatus.YELLOW -> "🟡"
        GoalStatus.RED -> "🔴"
    }
    val barColor = when (progress.status) {
        GoalStatus.GREEN -> AlertGreen
        GoalStatus.YELLOW -> AlertYellow
        GoalStatus.RED -> AlertRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(statusIcon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "儲蓄目標",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (progress.goal == null) {
                Text(
                    "尚未設定本月儲蓄目標",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        goalInput = ""
                        showSetGoalDialog = true
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("＋ 設定目標")
                }
            } else {
                val target = progress.goal!!.targetAmount
                val actual = progress.actualSavings
                val pct = if (target > 0) (actual / target).coerceIn(0.0, 1.5) else 0.0

                Text("目標：${String.format("%.0f", target)}", fontSize = 14.sp, color = TextPrimary)
                Text(
                    "目前儲蓄：${String.format("%.0f", actual)}",
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            Modifier.padding(0.dp)
                        )
                ) {
                    LinearProgressIndicator(
                        progress = { pct.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = barColor,
                        trackColor = Color.White.copy(alpha = 0.6f),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (progress.status) {
                    GoalStatus.GREEN -> {
                        Text(
                            "✅ 達標！繼續保持～",
                            color = AlertGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    GoalStatus.YELLOW -> {
                        Text(
                            "⚠️ 尚未達標（已記錄原因：${progress.goal!!.reason}）",
                            color = Color(0xFFF57F17),
                            fontSize = 14.sp
                        )
                    }
                    GoalStatus.RED -> {
                        Text(
                            "❌ 尚未達標，距離目標還差 ${String.format("%.0f", target - actual)}",
                            color = AlertRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = {
                                reasonInput = progress.goal!!.reason ?: ""
                                showReasonDialog = true
                            }
                        ) {
                            Text("填寫原因", color = AlertRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        goalInput = progress.goal!!.targetAmount.toString()
                        showSetGoalDialog = true
                    }
                ) {
                    Text("編輯目標", fontSize = 13.sp, color = TextSecondary)
                }
            }
        }
    }

    // === Dialog: set/edit goal ===
    if (showSetGoalDialog) {
        AlertDialog(
            onDismissRequest = { showSetGoalDialog = false },
            title = { Text("設定儲蓄目標 💰") },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    label = { Text("每月儲蓄目標金額") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Lavender,
                        cursorColor = Lavender
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = goalInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            val month1Based = (selectedMonth ?: 0) + 1
                            viewModel.setSavingsGoal(selectedYear, month1Based, amount)
                        }
                        showSetGoalDialog = false
                    },
                    enabled = (goalInput.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    Text("儲存", color = Lavender)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetGoalDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    // === Dialog: enter reason ===
    if (showReasonDialog) {
        AlertDialog(
            onDismissRequest = { showReasonDialog = false },
            title = { Text("未達標原因 📝") },
            text = {
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("例如：醫療支出、旅遊花費") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AlertRed,
                        cursorColor = AlertRed
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val month1Based = (selectedMonth ?: 0) + 1
                        viewModel.updateGoalReason(selectedYear, month1Based, reasonInput)
                        showReasonDialog = false
                    },
                    enabled = reasonInput.isNotBlank()
                ) {
                    Text("儲存", color = AlertRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReasonDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}
