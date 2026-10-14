package com.lzy.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lzy.myapplication.ui.navigation.AppNavigation
import com.lzy.myapplication.ui.theme.PiggyTheme
import com.lzy.myapplication.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PiggyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: ExpenseViewModel = viewModel()
                    val expenses = viewModel.allExpenses.collectAsState(initial = emptyList())
                    val balance = viewModel.balance.collectAsState(initial = 0.0)
                    val income = viewModel.totalIncome.collectAsState(initial = 0.0)
                    val totalExpenseState = viewModel.totalExpense.collectAsState(initial = 0.0)
                    AppNavigation(
                        expenses = expenses.value,
                        balance = balance.value,
                        income = income.value,
                        totalExpense = totalExpenseState.value,
                        onAddExpense = { amount, category, note, isIncome ->
                            viewModel.addExpense(amount, category, note, isIncome)
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )
                }
            }
        }
    }
}
