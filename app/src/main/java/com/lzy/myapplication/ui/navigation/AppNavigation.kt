package com.lzy.myapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lzy.myapplication.data.Expense
import com.lzy.myapplication.ui.screens.AddExpenseScreen
import com.lzy.myapplication.ui.screens.HomeScreen
import com.lzy.myapplication.ui.screens.StatisticsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "首頁", Icons.Default.Home)
    object Statistics : Screen("statistics", "統計", Icons.Default.BarChart)
}

@Composable
fun AppNavigation(
    expenses: List<Expense>,
    balance: Double,
    income: Double,
    totalExpense: Double,
    onAddExpense: (Double, com.lzy.myapplication.data.ExpenseCategory, String, Boolean) -> Unit,
    onDeleteExpense: (Expense) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(Screen.Home, Screen.Statistics)

    // 排除 AddExpense 路由不顯示底部欄
    val showBottomBar = currentRoute != "add_expense"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    tonalElevation = 4.dp
                ) {
                    screens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.lzy.myapplication.ui.theme.Coral,
                                selectedTextColor = com.lzy.myapplication.ui.theme.Coral,
                                unselectedIconColor = com.lzy.myapplication.ui.theme.TextLight,
                                unselectedTextColor = com.lzy.myapplication.ui.theme.TextLight,
                                indicatorColor = com.lzy.myapplication.ui.theme.Coral.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    expenses = expenses,
                    balance = balance,
                    income = income,
                    expense = totalExpense,
                    onAddClick = { navController.navigate("add_expense") },
                    onDeleteExpense = onDeleteExpense
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    expenses = expenses,
                    totalExpense = totalExpense
                )
            }
            composable("add_expense") {
                AddExpenseScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { amount, category, note, isIncome ->
                        onAddExpense(amount, category, note, isIncome)
                    }
                )
            }
        }
    }
}
