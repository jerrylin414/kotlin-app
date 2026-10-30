package com.lzy.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GoalStatus {
    RED,      // 未達標，未填寫原因
    YELLOW,   // 未達標，已填寫原因
    GREEN     // 已達標或未設定目標
}

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetAmount: Double,
    val year: Int,
    val month: Int,        // 1-12
    val reason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
