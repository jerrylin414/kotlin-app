package com.lzy.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ChallengeType {
    NONE,           // 無挑戰
    DAY_365,        // 365存錢法：第N天存N元
    WEEK_52,        // 52週存錢法：第N週存N*10元
    FIXED_DAILY,    // 每日固定金額
    FIXED_WEEKLY    // 每週固定金額
}

@Entity(tableName = "piggy_banks")
data class PiggyBank(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val color: Long = 0xFFFFB5BA,
    val targetDate: Long? = null,           // 目標達成日期（可選）
    val challengeType: String = ChallengeType.NONE.name,
    val fixedAmount: Double = 0.0,           // 固定存入金額（挑戰用）
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "piggy_bank_transactions")
data class PiggyBankTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val piggyBankId: Long,
    val amount: Double,
    val type: String,                       // "DEPOSIT" or "WITHDRAW"
    val note: String? = null,
    val date: Long = System.currentTimeMillis()
)
