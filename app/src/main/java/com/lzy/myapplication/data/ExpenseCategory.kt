package com.lzy.myapplication.data

import androidx.compose.ui.graphics.Color
import com.lzy.myapplication.ui.theme.*

enum class ExpenseCategory(
    val emoji: String,
    val label: String,
    val color: Color
) {
    FOOD("🍱", "美食", PeachPink),
    TRANSPORT("🚗", "交通", BabyBlue),
    SHOPPING("🛍️", "購物", Lavender),
    ENTERTAINMENT("🎮", "娛樂", SoftMint),
    HOUSING("🏠", "住房", Coral),
    MEDICAL("💊", "醫療", PeachPinkDark),
    EDUCATION("📚", "教育", BabyBlueDark),
    PET("🐱", "寵物", CreamYellow),
    SALARY("💰", "工資", SoftMint),
    BONUS("🎁", "獎金", Coral),
    OTHER("📦", "其他", Lavender);

    companion object {
        val expenseCategories = listOf(
            FOOD, TRANSPORT, SHOPPING, ENTERTAINMENT,
            HOUSING, MEDICAL, EDUCATION, PET, OTHER
        )
        val incomeCategories = listOf(SALARY, BONUS, OTHER)
    }
}
