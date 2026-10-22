package com.lzy.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 数据库操作接口 🔍
 * 定义了对 meeting_records 表的所有操作
 */
@Dao
interface MeetingDao {

    /**
     * 获取所有会议记录
     * Flow 意味着数据库有变化时会自动通知 UI 更新
     * 按时间倒序排列（最新的排在前面）
     */
    @Query("SELECT * FROM meeting_records ORDER BY date DESC")
    fun getAllMeetings(): Flow<List<MeetingRecord>>

    /**
     * 插入一条新记录
     */
    @Insert
    suspend fun insert(record: MeetingRecord)

    /**
     * 根据 id 删除一条记录
     */
    @Query("DELETE FROM meeting_records WHERE id = :id")
    suspend fun deleteById(id: Int)
}
