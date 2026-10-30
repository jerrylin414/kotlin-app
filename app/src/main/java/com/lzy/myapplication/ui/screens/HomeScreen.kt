package com.lzy.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lzy.myapplication.data.Expense
import com.lzy.myapplication.data.ExpenseCategory
import com.lzy.myapplication.data.Folder
import com.lzy.myapplication.ui.components.AnimalAvatar
import com.lzy.myapplication.ui.components.BalanceCard
import com.lzy.myapplication.ui.components.ConfirmDeleteDialog
import com.lzy.myapplication.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// =============================================
// HomeScreen — 資料夾列表
// =============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    expenses: List<Expense>,
    balance: Double,
    income: Double,
    expense: Double,
    onAddClick: () -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    folders: List<Folder> = emptyList(),
    onFolderClick: (Folder) -> Unit = {},
    onCreateFolder: (String) -> Unit = {},
    onRenameFolder: (Folder, String) -> Unit = { _, _ -> },
    onDeleteFolder: (Folder) -> Unit = {}
) {
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var showDeleteFolderDialog by remember { mutableStateOf(false) }
    var folderToDelete by remember { mutableStateOf<Folder?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var folderToRename by remember { mutableStateOf<Folder?>(null) }
    var renameText by remember { mutableStateOf("") }

    // 計算每個文件夾的餘額
    val folderBalances = remember(expenses, folders) {
        folders.associate { folder ->
            val folderExpenses = expenses.filter { it.folderId == folder.id }
            val inc = folderExpenses.filter { it.isIncome }.sumOf { it.amount }
            val exp = folderExpenses.filter { !it.isIncome }.sumOf { it.amount }
            folder.id to (inc - exp)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐷 ", fontSize = 22.sp)
                        Text(
                            "小豬記賬",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateFolderDialog = true },
                containerColor = Lavender,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "新增資料夾")
            }
        },
        containerColor = Color(0xFFFFFBFF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 餘額卡片
            item {
                BalanceCard(
                    balance = balance,
                    income = income,
                    expense = expense
                )
            }

            // 分隔標題
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📁 我的資料夾",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${folders.size} 個)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 空狀態
            if (folders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📁", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "還沒有資料夾哦～",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                            Text(
                                "點擊右下角 + 新增資料夾吧！",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }
                    }
                }
            }

            // 全部記錄入口
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clickable { /* 可選：顯示所有記錄 */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Lavender.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📄", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "全部記錄",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                "${expenses.size} 筆",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            String.format("%.2f", balance),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (balance >= 0) SoftMint else Coral
                        )
                    }
                }
            }

            // 資料夾列表
            items(folders, key = { it.id }) { folder ->
                val folderExpenseCount = expenses.count { it.folderId == folder.id }
                val folderBalance = folderBalances[folder.id] ?: 0.0

                FolderCard(
                    folder = folder,
                    expenseCount = folderExpenseCount,
                    balance = folderBalance,
                    onClick = { onFolderClick(folder) },
                    onRename = { f ->
                        folderToRename = f
                        renameText = f.name
                        showRenameDialog = true
                    },
                    onDelete = {
                        folderToDelete = folder
                        showDeleteFolderDialog = true
                    }
                )
            }
        }
    }

    // 新增資料夾對話框
    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("新增資料夾 📁") },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("資料夾名稱") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Lavender,
                        cursorColor = Lavender
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            onCreateFolder(newFolderName.trim())
                            newFolderName = ""
                            showCreateFolderDialog = false
                        }
                    },
                    enabled = newFolderName.isNotBlank()
                ) {
                    Text("新增", color = Lavender)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateFolderDialog = false
                    newFolderName = ""
                }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    // 改名對話框
    if (showRenameDialog && folderToRename != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("重新命名資料夾 ✏️") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("資料夾名稱") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Lavender,
                        cursorColor = Lavender
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            onRenameFolder(folderToRename!!, renameText.trim())
                            showRenameDialog = false
                            folderToRename = null
                            renameText = ""
                        }
                    },
                    enabled = renameText.isNotBlank()
                ) {
                    Text("儲存", color = Lavender)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    folderToRename = null
                    renameText = ""
                }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }

    // 刪除資料夾對話框
    if (showDeleteFolderDialog && folderToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteFolderDialog = false
                folderToDelete = null
            },
            title = { Text("確認刪除資料夾？") },
            text = { Text("確定要刪除資料夾「${folderToDelete?.name}」嗎？資料夾內的記錄不會被刪除。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        folderToDelete?.let { onDeleteFolder(it) }
                        showDeleteFolderDialog = false
                        folderToDelete = null
                    }
                ) {
                    Text("確認刪除", color = Coral)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteFolderDialog = false
                    folderToDelete = null
                }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}

// =============================================
// FolderCard — 帶有編輯/刪除按鈕的文件夾卡片
// =============================================
@Composable
private fun FolderCard(
    folder: Folder,
    expenseCount: Int,
    balance: Double,
    onClick: () -> Unit,
    onRename: (Folder) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 資料夾圖標
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Lavender.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = Lavender,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    folder.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "$expenseCount 筆記錄",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Text(
                String.format("%.2f", balance),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (balance >= 0) SoftMint else Coral
            )

            // 更多操作選單
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多操作",
                        tint = TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("✏️ 重新命名") },
                        onClick = {
                            showMenu = false
                            onRename(folder)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🗑️ 刪除資料夾") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

// =============================================
// FolderDetailScreen — 資料夾內的記錄列表
// =============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folder: Folder,
    folderExpenses: List<Expense>,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteExpense: (Expense) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val income = folderExpenses.filter { it.isIncome }.sumOf { it.amount }
    val expense = folderExpenses.filter { !it.isIncome }.sumOf { it.amount }
    val balance = income - expense

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📁 ", fontSize = 22.sp)
                        Text(
                            folder.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Coral,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "記一筆")
            }
        },
        containerColor = Color(0xFFFFFBFF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 文件夾摘要卡片
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Lavender.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📁 ${folder.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            String.format("%.2f", balance),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balance >= 0) SoftMintDark else Coral
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📥 收入", fontSize = 13.sp, color = TextSecondary)
                                Text(
                                    String.format("+%.2f", income),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftMintDark
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(TextLight)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📤 支出", fontSize = 13.sp, color = TextSecondary)
                                Text(
                                    String.format("-%.2f", expense),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Coral
                                )
                            }
                        }
                    }
                }
            }

            // 記錄數量
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 收支明細",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${folderExpenses.size} 筆)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 空狀態
            if (folderExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐷", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "這個資料夾還沒有記錄哦～",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                            Text(
                                "點擊右下角 + 記第一筆吧！",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }
                    }
                }
            }

            // 記錄列表
            items(folderExpenses, key = { it.id }) { expenseItem ->
                ExpenseItem(
                    expense = expenseItem,
                    onDeleteClick = {
                        expenseToDelete = expenseItem
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    // 刪除確認對話框
    if (showDeleteDialog && expenseToDelete != null) {
        ConfirmDeleteDialog(
            onConfirm = {
                expenseToDelete?.let { onDeleteExpense(it) }
                showDeleteDialog = false
                expenseToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                expenseToDelete = null
            }
        )
    }
}

// =============================================
// ExpenseItem — 單條記錄
// =============================================
@Composable
fun ExpenseItem(
    expense: Expense,
    onDeleteClick: () -> Unit
) {
    val category = try {
        ExpenseCategory.valueOf(expense.category)
    } catch (e: Exception) {
        ExpenseCategory.OTHER
    }

    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(expense.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 動物頭像（分類圖標）
            AnimalAvatar(
                emoji = category.emoji,
                backgroundColor = category.color.copy(alpha = 0.3f),
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                if (expense.note.isNotBlank()) {
                    Text(
                        text = expense.note,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextLight
                )
            }

            // 金額
            Text(
                text = if (expense.isIncome) "+${expense.amount}" else "-${expense.amount}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (expense.isIncome) SoftMint else Coral
            )

            // 刪除按鈕
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "刪除",
                    tint = TextLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
