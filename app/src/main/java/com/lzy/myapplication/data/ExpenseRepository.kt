package com.lzy.myapplication.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    val allIncome: Flow<List<Expense>> = dao.getAllIncome()
    val allExpenseEntries: Flow<List<Expense>> = dao.getAllExpenseEntries()

    suspend fun insert(expense: Expense): Long = dao.insert(expense)
    suspend fun update(expense: Expense) = dao.update(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
}
