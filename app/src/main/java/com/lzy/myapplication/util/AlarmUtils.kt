package com.lzy.myapplication.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 震动提醒工具 📳
 * 时间到了让手机震动一下
 */
object AlarmUtils {

    /**
     * 触发震动
     * @param context 上下文，用来获取系统服务
     */
    fun vibrate(context: Context) {
        // 根据 Android 版本不同，获取震动器的方式不一样
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 用 VibratorManager
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            // 旧版本直接用 Vibrator
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // 震动 500 毫秒
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
}
