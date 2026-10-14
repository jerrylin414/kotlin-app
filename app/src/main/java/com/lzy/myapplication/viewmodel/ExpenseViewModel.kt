package com.lzy.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lzy.myapplication.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository

    val allExpenses: StateFlow<List<Expense>>
    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>
    val balance: StateFlow<Double>

    init {
        val dao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)

        allExpenses = repository.allExpenses
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        totalIncome = repository.allIncome
            .map { list -> list.sumOf { it.amount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalExpense = repository.allExpenseEntries
            .map { list -> list.sumOf { it.amount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        balance = combine(totalIncome, totalExpense) { income, expense ->
            income - expense
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    fun addExpense(amount: Double, category: ExpenseCategory, note: String, isIncome: Boolean) {
        viewModelScope.launch {
            repository.insert(
                Expense(
                    amount = amount,
                    category = category.name,
                    note = note,
                    isIncome = isIncome
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }
}
