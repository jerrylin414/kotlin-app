package com.lzy.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lzy.myapplication.navigation.AppNavigation
import com.lzy.myapplication.ui.theme.MeetingTheme
import com.lzy.myapplication.viewmodel.MeetingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 套用商务风格主题 💼
            MeetingTheme {
                // 创建 ViewModel（管理所有页面状态）
                val viewModel: MeetingViewModel = viewModel()
                // 启动导航（显示各个页面）
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
