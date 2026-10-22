package com.lzy.myapplication.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lzy.myapplication.data.MeetingRecord
import com.lzy.myapplication.ui.theme.AccentGreen
import com.lzy.myapplication.ui.theme.AccentOrange
import com.lzy.myapplication.ui.theme.AccentRed
import com.lzy.myapplication.util.TimeUtils

/**
 * 会议记录卡片 📇
 * 在历史页面中展示一条会议记录
 */
@Composable
fun MeetingCard(
    record: MeetingRecord,
    onDelete: () -> Unit
) {
    val actualMinutes = record.actualSeconds / 60
    val diff = actualMinutes - record.plannedMinutes

    // 根据超时程度选颜色
    val (statusColor, statusText) = when {
        diff > 15 -> Pair(AccentRed, "超时 +${diff}分钟")
        diff > 0 -> Pair(AccentOrange, "超时 +${diff}分钟")
        diff < -5 -> Pair(AccentGreen, "提前 ${-diff}分钟")
        else -> Pair(AccentGreen, "准时结束")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 第一行：会议名称 + 删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.name,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "删除")
                }
            }

            // 日期
            Text(
                text = TimeUtils.formatDate(record.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 时长对比
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("计划: ${record.plannedMinutes}分钟")
                Text("实际: ${actualMinutes}分钟")
            }

            // 状态标签
            Text(
                text = statusText,
                color = statusColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
