package com.lzy.myapplication.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    // === Expense ===
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    val allIncome: Flow<List<Expense>> = dao.getAllIncome()
    val allExpenseEntries: Flow<List<Expense>> = dao.getAllExpenseEntries()

    fun getIncomeInRange(start: Long, end: Long): Flow<List<Expense>> = dao.getIncomeInRange(start, end)
    fun getExpenseInRange(start: Long, end: Long): Flow<List<Expense>> = dao.getExpenseInRange(start, end)
    fun getExpensesInRange(start: Long, end: Long): Flow<List<Expense>> = dao.getExpensesInRange(start, end)
    fun getExpensesByFolder(folderId: Long): Flow<List<Expense>> = dao.getExpensesByFolder(folderId)

    suspend fun insert(expense: Expense): Long = dao.insert(expense)
    suspend fun update(expense: Expense) = dao.update(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)

    // === Folder ===
    val allFolders: Flow<List<Folder>> = dao.getAllFolders()
    suspend fun insertFolder(folder: Folder): Long = dao.insertFolder(folder)
    suspend fun updateFolder(folder: Folder) = dao.updateFolder(folder)
    suspend fun deleteFolder(folder: Folder) {
        dao.deleteExpensesByFolder(folder.id)
        dao.deleteFolder(folder)
    }

    // === SavingsGoal ===
    suspend fun getSavingsGoalForMonth(year: Int, month: Int): SavingsGoal? = dao.getSavingsGoalForMonth(year, month)
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long = dao.insertSavingsGoal(goal)
    suspend fun updateSavingsGoal(goal: SavingsGoal) = dao.updateSavingsGoal(goal)

    // === PiggyBank ===
    val allPiggyBanks: Flow<List<PiggyBank>> = dao.getAllPiggyBanks()
    suspend fun insertPiggyBank(piggyBank: PiggyBank): Long = dao.insertPiggyBank(piggyBank)
    suspend fun updatePiggyBank(piggyBank: PiggyBank) = dao.updatePiggyBank(piggyBank)
    suspend fun deletePiggyBank(piggyBank: PiggyBank) = dao.deletePiggyBank(piggyBank)
    fun getTransactionsForPiggyBank(piggyBankId: Long): Flow<List<PiggyBankTransaction>> = dao.getTransactionsForPiggyBank(piggyBankId)
    suspend fun insertTransaction(transaction: PiggyBankTransaction): Long = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: PiggyBankTransaction) = dao.deleteTransaction(transaction)
}
