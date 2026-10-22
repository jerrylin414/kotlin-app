package com.lzy.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lzy.myapplication.data.MeetingDatabase
import com.lzy.myapplication.data.MeetingRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 会议 ViewModel 🧠
 * 管理所有页面需要用到的状态和操作
 * 继承 AndroidViewModel 是为了能拿到 Application 上下文（用来访问数据库）
 */
class MeetingViewModel(application: Application) : AndroidViewModel(application) {

    // ===== 数据库相关 =====
    private val database = MeetingDatabase.getDatabase(application)
    private val dao = database.meetingDao()

    /**
     * 所有会议记录（Flow 类型）
     * 数据库有变化时 UI 会自动更新，不需要手动刷新
     */
    val allMeetings = dao.getAllMeetings()


    // ===== 计时器状态 =====

    /** 剩余秒数 */
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    /** 总秒数（用户设置的会议时长） */
    private val _totalSeconds = MutableStateFlow(0)
    val totalSeconds: StateFlow<Int> = _totalSeconds.asStateFlow()

    /** 计时器是否在运行中 */
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    /** 时间是否到了（剩余秒数为 0 且之前设置过时长） */
    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    /** 会议名称 */
    private val _meetingName = MutableStateFlow("")
    val meetingName: StateFlow<String> = _meetingName.asStateFlow()

    /** 计时器任务（用来取消定时） */
    private var timerJob: Job? = null

    /** 记录开始时间（用来算实际用时） */
    private var startTime: Long = 0L


    // ===== 计时器操作 =====

    /** 设置会议名称 */
    fun setMeetingName(name: String) {
        _meetingName.value = name
    }

    /** 设置会议时长（分钟） */
    fun setDuration(minutes: Int) {
        // 如果正在运行中就不能改了
        if (_isRunning.value) return
        val seconds = minutes * 60
        _totalSeconds.value = seconds
        _remainingSeconds.value = seconds
        _isFinished.value = false
    }

    /** 开始计时 */
    fun start() {
        // 如果剩余时间是 0，不能开始
        if (_remainingSeconds.value <= 0) return

        _isRunning.value = true
        _isFinished.value = false
        startTime = System.currentTimeMillis()  // 记录开始时间

        // 启动一个协程，每秒钟减 1
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && _isRunning.value) {
                delay(1000)  // 等 1 秒
                if (_isRunning.value) {
                    _remainingSeconds.value -= 1
                }
            }
            // 倒计时结束
            if (_remainingSeconds.value <= 0) {
                _isFinished.value = true
                _isRunning.value = false
            }
        }
    }

    /** 暂停计时 */
    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()  // 取消定时任务
    }

    /** 重置计时器 */
    fun reset() {
        timerJob?.cancel()
        _isRunning.value = false
        _isFinished.value = false
        _remainingSeconds.value = _totalSeconds.value  // 恢复到用户设置的时长
    }

    /** 结束会议（保存记录到数据库） */
    fun finishMeeting() {
        // 计算实际用时（秒）
        val actualSeconds = if (startTime > 0) {
            ((System.currentTimeMillis() - startTime) / 1000).toInt()
        } else {
            (_totalSeconds.value - _remainingSeconds.value)  // 备用算法
        }

        // 创建一条记录
        val record = MeetingRecord(
            name = _meetingName.value.ifEmpty { "未命名会议" },
            plannedMinutes = _totalSeconds.value / 60,
            actualSeconds = actualSeconds,
            date = System.currentTimeMillis()
        )

        // 保存到数据库
        viewModelScope.launch {
            dao.insert(record)
        }

        // 重置计时器
        reset()
    }


    // ===== 数据库操作 =====

    /** 删除一条会议记录 */
    fun deleteMeeting(id: Int) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }


    // ===== 统计相关 =====

    /** 计算今日总时长（分钟） */
    fun todayTotalMinutes(meetings: List<MeetingRecord>): Int {
        val now = System.currentTimeMillis()
        // 今天 0 点的时间戳
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis

        // 筛选今天的会议，把时长加起来
        return meetings
            .filter { it.date >= startOfDay }
            .sumOf { it.actualSeconds / 60 }
    }

    /** 计算本周总时长（分钟） */
    fun weekTotalMinutes(meetings: List<MeetingRecord>): Int {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfWeek = cal.timeInMillis

        return meetings
            .filter { it.date >= startOfWeek }
            .sumOf { it.actualSeconds / 60 }
    }

    /** 今日会议数量 */
    fun todayCount(meetings: List<MeetingRecord>): Int {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return meetings.count { it.date >= cal.timeInMillis }
    }

    /** 本周会议数量 */
    fun weekCount(meetings: List<MeetingRecord>): Int {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return meetings.count { it.date >= cal.timeInMillis }
    }
}
