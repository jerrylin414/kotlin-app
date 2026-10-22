package com.lzy.myapplication.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间格式化工具 ⏰
 * 把数字转成人能看懂的格式
 */
object TimeUtils {

    /**
     * 把秒数转成 "25:00" 这样的格式
     * @param seconds 总秒数
     * @return 格式字符串，比如 90秒 → "01:30"
     */
    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60        // 算出分钟
        val secs = seconds % 60        // 算出剩余秒数
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    /**
     * 把时间戳转成 "2026-10-21 14:30" 这样的格式
     * @param timestamp 时间戳（毫秒）
     * @return 日期时间字符串
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
