package com.lzy.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lzy.myapplication.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class FilterMode { ALL_TIME, YEAR, MONTH }

data class GoalProgress(
    val goal: SavingsGoal?,
    val actualSavings: Double,
    val status: GoalStatus
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository

    // === Existing state ===
    val allExpenses: StateFlow<List<Expense>>
    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>
    val balance: StateFlow<Double>

    // === Folders ===
    val allFolders: StateFlow<List<Folder>>

    // === Filter state ===
    private val _filterMode = MutableStateFlow(FilterMode.ALL_TIME)
    val filterMode: StateFlow<FilterMode> = _filterMode

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear

    private val _selectedMonth = MutableStateFlow<Int?>(null)
    val selectedMonth: StateFlow<Int?> = _selectedMonth

    // === Filtered statistics ===
    val filteredIncome: StateFlow<Double>
    val filteredExpense: StateFlow<Double>
    val filteredExpenses: StateFlow<List<Expense>>

    // === Refresh trigger for goal progress ===
    private val _goalRefreshTrigger = MutableStateFlow(0L)

    // === Savings goal ===
    val goalProgress: StateFlow<GoalProgress?>

    // === Piggy banks ===
    val allPiggyBanks: StateFlow<List<PiggyBank>>

    // === Folder detail ===
    fun getExpensesByFolder(folderId: Flow<Long?>): Flow<List<Expense>> {
        return folderId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getExpensesByFolder(id)
        }
    }

    init {
        val dao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)

        // Existing
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

        // Folders
        allFolders = repository.allFolders
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Filtered statistics — recompute whenever filter changes
        val dateRangeFlow = combine(_filterMode, _selectedYear, _selectedMonth) { mode, year, month ->
            calculateDateRange(mode, year, month)
        }

        filteredIncome = dateRangeFlow.flatMapLatest { range ->
            repository.getIncomeInRange(range.first, range.second)
                .map { list -> list.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        filteredExpense = dateRangeFlow.flatMapLatest { range ->
            repository.getExpenseInRange(range.first, range.second)
                .map { list -> list.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        filteredExpenses = dateRangeFlow.flatMapLatest { range ->
            repository.getExpensesInRange(range.first, range.second)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Goal progress — recompute when filter changes OR when refresh triggered
        goalProgress = combine(dateRangeFlow, _selectedMonth, _goalRefreshTrigger) { range, month, _ ->
            if (month == null) {
                null
            } else {
                val year = _selectedYear.value
                val month1Based = month + 1  // Calendar.MONTH is 0-based, we store 1-based
                val goal = repository.getSavingsGoalForMonth(year, month1Based)

                val incomeInRange = repository.getIncomeInRange(range.first, range.second)
                    .first().sumOf { it.amount }
                val expenseInRange = repository.getExpenseInRange(range.first, range.second)
                    .first().sumOf { it.amount }
                val actualSavings = incomeInRange - expenseInRange

                val status = when {
                    goal == null -> GoalStatus.GREEN
                    actualSavings >= goal.targetAmount -> GoalStatus.GREEN
                    goal.reason.isNullOrBlank() -> GoalStatus.RED
                    else -> GoalStatus.YELLOW
                }

                GoalProgress(goal, actualSavings, status)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        // Piggy banks
        allPiggyBanks = repository.allPiggyBanks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // === Expense operations ===
    fun addExpense(amount: Double, category: ExpenseCategory, note: String, isIncome: Boolean, folderId: Long? = null) {
        viewModelScope.launch {
            repository.insert(
                Expense(
                    amount = amount,
                    category = category.name,
                    note = note,
                    isIncome = isIncome,
                    folderId = folderId
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    // === Folder operations ===
    fun addFolder(name: String) {
        viewModelScope.launch {
            repository.insertFolder(Folder(name = name))
        }
    }

    fun updateFolder(folder: Folder, newName: String) {
        viewModelScope.launch {
            repository.updateFolder(folder.copy(name = newName))
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
        }
    }

    // === Filter operations ===
    fun setFilterMode(mode: FilterMode, year: Int? = null, month: Int? = null) {
        _filterMode.value = mode
        year?.let { _selectedYear.value = it }
        _selectedMonth.value = month
    }

    // === Savings goal operations ===
    fun setSavingsGoal(year: Int, month: Int, amount: Double) {
        viewModelScope.launch {
            val existing = repository.getSavingsGoalForMonth(year, month)
            if (existing != null) {
                repository.updateSavingsGoal(existing.copy(targetAmount = amount))
            } else {
                repository.insertSavingsGoal(
                    SavingsGoal(targetAmount = amount, year = year, month = month)
                )
            }
            _goalRefreshTrigger.value = System.currentTimeMillis()
        }
    }

    fun updateGoalReason(year: Int, month: Int, reason: String) {
        viewModelScope.launch {
            val existing = repository.getSavingsGoalForMonth(year, month) ?: return@launch
            repository.updateSavingsGoal(existing.copy(reason = reason.ifBlank { null }))
            _goalRefreshTrigger.value = System.currentTimeMillis()
        }
    }

    // === Piggy bank operations ===
    fun addPiggyBank(
        name: String,
        emoji: String,
        targetAmount: Double,
        color: Long,
        targetDate: Long? = null,
        challengeType: ChallengeType = ChallengeType.NONE,
        fixedAmount: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.insertPiggyBank(
                PiggyBank(
                    name = name,
                    emoji = emoji,
                    targetAmount = targetAmount,
                    color = color,
                    targetDate = targetDate,
                    challengeType = challengeType.name,
                    fixedAmount = fixedAmount
                )
            )
        }
    }

    fun deletePiggyBank(piggyBank: PiggyBank) {
        viewModelScope.launch {
            repository.deletePiggyBank(piggyBank)
        }
    }

    fun deposit(piggyBank: PiggyBank, amount: Double, note: String? = null) {
        viewModelScope.launch {
            val newAmount = piggyBank.currentAmount + amount
            repository.updatePiggyBank(piggyBank.copy(currentAmount = newAmount))
            repository.insertTransaction(
                PiggyBankTransaction(
                    piggyBankId = piggyBank.id,
                    amount = amount,
                    type = "DEPOSIT",
                    note = note
                )
            )
        }
    }

    fun withdraw(piggyBank: PiggyBank, amount: Double, note: String? = null) {
        viewModelScope.launch {
            val newAmount = (piggyBank.currentAmount - amount).coerceAtLeast(0.0)
            repository.updatePiggyBank(piggyBank.copy(currentAmount = newAmount))
            repository.insertTransaction(
                PiggyBankTransaction(
                    piggyBankId = piggyBank.id,
                    amount = amount,
                    type = "WITHDRAW",
                    note = note
                )
            )
        }
    }

    fun getTransactionsForPiggyBank(piggyBankId: Long): Flow<List<PiggyBankTransaction>> {
        return repository.getTransactionsForPiggyBank(piggyBankId)
    }

    // === Helper ===
    private fun calculateDateRange(mode: FilterMode, year: Int, month: Int?): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        return when (mode) {
            FilterMode.ALL_TIME -> {
                0L to Long.MAX_VALUE
            }
            FilterMode.YEAR -> {
                cal.set(year, 0, 1, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(year, 11, 31, 23, 59, 59)
                cal.set(Calendar.MILLISECOND, 999)
                start to cal.timeInMillis
            }
            FilterMode.MONTH -> {
                val calMonth = (month ?: 0) // 0-based
                cal.set(year, calMonth, 1, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(year, calMonth, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                cal.set(Calendar.MILLISECOND, 999)
                start to cal.timeInMillis
            }
        }
    }
}
