package com.lzy.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String = "",
    val isIncome: Boolean = false,
    val date: Long = System.currentTimeMillis(),
    val folderId: Long? = null
)
