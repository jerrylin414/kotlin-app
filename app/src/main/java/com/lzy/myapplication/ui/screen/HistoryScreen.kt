package com.lzy.myapplication.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lzy.myapplication.ui.component.MeetingCard
import com.lzy.myapplication.viewmodel.MeetingViewModel

/**
 * 历史记录屏幕 📋
 * 展示所有已保存的会议记录
 */
@Composable
fun HistoryScreen(viewModel: MeetingViewModel) {
    // 获取所有会议记录
    val meetings by viewModel.allMeetings.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    if (meetings.isEmpty()) {
        // 没有记录时显示空状态
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📝 还没有会议记录\n开始你的第一场会议吧！",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // 有记录就用列表展示
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(meetings, key = { it.id }) { record ->
                MeetingCard(
                    record = record,
                    onDelete = { viewModel.deleteMeeting(record.id) }
                )
            }
        }
    }
}
