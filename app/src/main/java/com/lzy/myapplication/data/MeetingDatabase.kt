package com.lzy.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 数据库单例 📦
 * 整个 App 只有一个数据库实例
 */
@Database(entities = [MeetingRecord::class], version = 1, exportSchema = false)
abstract class MeetingDatabase : RoomDatabase() {

    // 获取数据库操作接口
    abstract fun meetingDao(): MeetingDao

    companion object {
        @Volatile
        private var INSTANCE: MeetingDatabase? = null

        /**
         * 获取数据库实例（单例模式）
         * 如果已经创建过就直接返回，没有才新建
         */
        fun getDatabase(context: Context): MeetingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeetingDatabase::class.java,
                    "meeting_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
