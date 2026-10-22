package com.lzy.myapplication.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lzy.myapplication.ui.theme.PrimaryBlue

/**
 * 时长快捷选择按钮组 🎯
 * 一行显示多个时长选项，点击选中
 *
 * @param options 可选的分钟数列表，比如 [15, 30, 45, 60]
 * @param selected 当前选中的秒数
 * @param onSelect 用户点击某个选项后的回调
 * @enabled 是否可点击（计时中要禁用）
 */
// 新增这个专门用来预览的无参函数！加 @Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DurationChips(
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    enabled: Boolean = true
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { minutes ->
            FilterChip(
                selected = selected == minutes * 60,
                onClick = { onSelect(minutes) },
                label = { Text("${minutes}分钟") },
                enabled = enabled
            )
        }
    }
}
