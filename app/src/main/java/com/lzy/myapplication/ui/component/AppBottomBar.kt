package com.lzy.myapplication.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lzy.myapplication.navigation.Screen

/**
 * 底部导航栏 🧭
 * 三个Tab：计时器、历史记录、统计
 */

@Composable
fun AppBottomBar(navController: NavController) {
    // 定义三个导航项
    val items = listOf(
        BottomNavItem("计时", Icons.Filled.PlayArrow, Screen.Timer.route),
        BottomNavItem("历史", Icons.Filled.History, Screen.History.route),
        BottomNavItem("统计", Icons.Filled.QueryStats, Screen.Stats.route)
    )

    // 获取当前所在页面
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                // 判断当前是否在这个页面上
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // 跳转逻辑：避免重复创建页面
                        popUpTo(Screen.Timer.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/** 导航项数据类 */
private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
