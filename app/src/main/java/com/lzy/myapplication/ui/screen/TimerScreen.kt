package com.lzy.myapplication.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lzy.myapplication.ui.component.DurationChips
import com.lzy.myapplication.ui.theme.AccentRed
import com.lzy.myapplication.ui.theme.PrimaryBlue
import com.lzy.myapplication.ui.theme.AccentGreen
import com.lzy.myapplication.util.AlarmUtils
import com.lzy.myapplication.util.TimeUtils
import com.lzy.myapplication.viewmodel.MeetingViewModel

/**
 * 计时器主屏幕 ⏱️
 * 用户设置会议时长、开始倒计时、查看剩余时间
 */
@Composable
fun TimerScreen(viewModel: MeetingViewModel) {
    val context = LocalContext.current

    // 从 ViewModel 获取状态（会自动跟随变化）
    val remaining by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val total by viewModel.totalSeconds.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
    val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()
    val name by viewModel.meetingName.collectAsStateWithLifecycle()

    // 倒计时数字颜色：正常是蓝色，超时变红（带动画过渡）
    val timerColor by animateColorAsState(
        targetValue = if (isFinished) AccentRed else PrimaryBlue,
        label = "timerColor"
    )

    // 时间到了 → 震动提醒
    LaunchedEffect(isFinished) {
        if (isFinished) {
            AlarmUtils.vibrate(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 会议名称输入框
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.setMeetingName(it) },
            label = { Text("会议名称") },
            enabled = !isRunning,  // 计时中不能改名字
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 时长快捷选择按钮
        Text("选择时长", style = MaterialTheme.typography.labelLarge)
        DurationChips(
            options = listOf(15, 30, 45, 60, 90),
            selected = total,
            onSelect = { viewModel.setDuration(it) },
            enabled = !isRunning  // 计时中不能改时长
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 大倒计时数字
        Text(
            text = if (total > 0) TimeUtils.formatDuration(remaining) else "00:00",
            style = MaterialTheme.typography.displayLarge,
            color = timerColor
        )

        // 进度条（显示已经过了多少时间）
        val progress = if (total > 0) (total - remaining).toFloat() / total else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (isFinished) AccentRed else AccentGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 主按钮：开始 / 暂停
        Button(
            onClick = { if (isRunning) viewModel.pause() else viewModel.start() },
            enabled = total > 0,  // 没设置时长时禁用
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) AccentRed else PrimaryBlue
            )
        ) {
            Text(
                text = if (isRunning) "⏸️ 暂停" else "▶️ 开始",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // 重置按钮 + 结束按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.reset() },
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("🔄 重置")
            }
            Button(
                onClick = { viewModel.finishMeeting() },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text("✅ 结束并保存")
            }
        }

        // 时间到了提示卡片
        if (isFinished) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AccentRed.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "⏰ 时间到！点击「结束并保存」记录本次会议",
                    modifier = Modifier.padding(16.dp),
                    color = AccentRed,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
