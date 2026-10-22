package com.lzy.myapplication.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lzy.myapplication.ui.theme.AccentGreen
import com.lzy.myapplication.ui.theme.PrimaryBlue
import com.lzy.myapplication.viewmodel.MeetingViewModel

/**
 * 统计屏幕 📊
 * 展示今日和本周的会议数据
 */
@Composable
fun StatsScreen(viewModel: MeetingViewModel) {
    val meetings by viewModel.allMeetings.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("📊 会议统计", style = MaterialTheme.typography.headlineLarge)

        // 今日统计卡片
        StatCard(
            title = "🕐 今日",
            totalMinutes = viewModel.todayTotalMinutes(meetings),
            count = viewModel.todayCount(meetings),
            color = AccentGreen
        )

        // 本周统计卡片
        StatCard(
            title = "📅 本周",
            totalMinutes = viewModel.weekTotalMinutes(meetings),
            count = viewModel.weekCount(meetings),
            color = PrimaryBlue
        )
    }
}

/**
 * 统计卡片组件
 * 显示一个时间维度的统计数据
 */
@Composable
private fun StatCard(
    title: String,
    totalMinutes: Int,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("总时长: $totalMinutes 分钟", style = MaterialTheme.typography.bodyLarge)
            Text("会议数: $count 场", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
