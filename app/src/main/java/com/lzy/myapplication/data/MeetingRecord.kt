package com.lzy.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会议记录实体 📝
 * 对应数据库中的一张表，每条记录就是一场会议
 */
@Entity(tableName = "meeting_records")
data class MeetingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                    // 唯一编号，自动生成
    val name: String,                   // 会议名称，比如"周会"
    val plannedMinutes: Int,            // 计划时长（分钟）
    val actualSeconds: Int,             // 实际用时（秒）
    val date: Long                      // 会议时间（时间戳）
)
