package com.lzy.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    // === Expense queries ===
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isIncome = 1 ORDER BY date DESC")
    fun getAllIncome(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isIncome = 0 ORDER BY date DESC")
    fun getAllExpenseEntries(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isIncome = 1 AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomeInRange(start: Long, end: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isIncome = 0 AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpenseInRange(start: Long, end: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE folderId = :folderId ORDER BY date DESC")
    fun getExpensesByFolder(folderId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesInRange(start: Long, end: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    // === Folder queries ===
    @Query("SELECT * FROM folders ORDER BY createdAt DESC")
    fun getAllFolders(): Flow<List<Folder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    // === SavingsGoal queries ===
    @Query("SELECT * FROM savings_goals WHERE year = :year AND month = :month LIMIT 1")
    suspend fun getSavingsGoalForMonth(year: Int, month: Int): SavingsGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoal)
}
