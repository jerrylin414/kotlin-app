package com.lzy.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lzy.myapplication.ui.component.AppBottomBar
import com.lzy.myapplication.ui.screen.HistoryScreen
import com.lzy.myapplication.ui.screen.StatsScreen
import com.lzy.myapplication.ui.screen.TimerScreen
import com.lzy.myapplication.viewmodel.MeetingViewModel

/**
 * 路由定义 🗺️
 * sealed class 表示这几个页面是固定的，不会增加
 */
sealed class Screen(val route: String) {
    data object Timer : Screen("timer")      // 计时器页面
    data object History : Screen("history")  // 历史记录页面
    data object Stats : Screen("stats")      // 统计页面
}

/**
 * App 导航入口
 * 管理页面之间的跳转
 */
@Composable
fun AppNavigation(viewModel: MeetingViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Timer.route,  // 默认打开计时器页面
            modifier = Modifier.padding(innerPadding)
        ) {
            // 计时器页面
            composable(Screen.Timer.route) {
                TimerScreen(viewModel = viewModel)
            }
            // 历史记录页面
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel)
            }
            // 统计页面
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
        }
    }
}
