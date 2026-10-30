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
import com.lzy.myapplication.data.ExpenseCategory
import com.lzy.myapplication.data.Folder
import com.lzy.myapplication.ui.screens.AddExpenseScreen
import com.lzy.myapplication.ui.screens.FolderDetailScreen
import com.lzy.myapplication.ui.screens.HomeScreen
import com.lzy.myapplication.ui.screens.StatisticsScreen
import com.lzy.myapplication.viewmodel.ExpenseViewModel
import kotlinx.coroutines.flow.flowOf

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
    onAddExpense: (Double, ExpenseCategory, String, Boolean, Long?) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    viewModel: ExpenseViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(Screen.Home, Screen.Statistics)

    // 排除 add_expense 和 folder_detail 路由不顯示底部欄
    val showBottomBar = currentRoute != "add_expense/{folderId}" && currentRoute != "folder_detail/{folderId}/{folderName}"

    val folders by viewModel.allFolders.collectAsState()

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
                    onAddClick = { /* FAB now creates folders */ },
                    onDeleteExpense = onDeleteExpense,
                    folders = folders,
                    onFolderClick = { folder ->
                        navController.navigate("folder_detail/${folder.id}/${java.net.URLEncoder.encode(folder.name, "UTF-8")}")
                    },
                    onCreateFolder = { name -> viewModel.addFolder(name) },
                    onRenameFolder = { folder, newName -> viewModel.updateFolder(folder, newName) },
                    onDeleteFolder = { folder -> viewModel.deleteFolder(folder) }
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    viewModel = viewModel
                )
            }
            composable("add_expense/{folderId}") { backStackEntry ->
                val folderIdStr = backStackEntry.arguments?.getString("folderId")
                val folderId = if (folderIdStr == "none") null else folderIdStr?.toLongOrNull()
                AddExpenseScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { amount, category, note, isIncome, selectedFolderId ->
                        onAddExpense(amount, category, note, isIncome, selectedFolderId ?: folderId)
                    },
                    folders = folders,
                    onAddFolder = { name -> viewModel.addFolder(name) },
                    initialFolderId = folderId
                )
            }
            composable("folder_detail/{folderId}/{folderName}") { backStackEntry ->
                val folderId = backStackEntry.arguments?.getString("folderId")?.toLongOrNull() ?: 0L
                val folderName = try {
                    java.net.URLDecoder.decode(backStackEntry.arguments?.getString("folderName") ?: "", "UTF-8")
                } catch (e: Exception) {
                    backStackEntry.arguments?.getString("folderName") ?: ""
                }
                val folder = folders.find { it.id == folderId } ?: Folder(id = folderId, name = folderName)

                val folderExpensesFlow = remember(folderId) {
                    viewModel.getExpensesByFolder(flowOf(folderId))
                }
                val folderExpenses by folderExpensesFlow.collectAsState(initial = emptyList())

                FolderDetailScreen(
                    folder = folder,
                    folderExpenses = folderExpenses,
                    onBack = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate("add_expense/$folderId")
                    },
                    onDeleteExpense = onDeleteExpense
                )
            }
        }
    }
}
