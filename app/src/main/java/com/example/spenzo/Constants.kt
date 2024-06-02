package com.example.spenzo

import android.graphics.Color

object Constants {
    val transactionType = listOf("Income", "Expense")

    val transactionTags = listOf(
        "Housing",
        "Transportation",
        "Food",
        "Utilities",
        "Insurance",
        "Healthcare",
        "Saving & Debts",
        "Personal Spending",
        "Entertainment",
        "Miscellaneous"
    )

    private val categoryColors = mapOf(
        "Housing" to Color.parseColor("#FF33F5"),
        "Transportation" to Color.parseColor("#33FFF5"),
        "Food" to Color.parseColor("#3357FF"),
        "Utilities" to Color.parseColor("#FF33A1"),
        "Insurance" to Color.parseColor("#FFB533"),
        "Healthcare" to Color.parseColor("#B533FF"),
        "Saving & Debts" to Color.parseColor("#6FCF97"),
        "Personal Spending" to Color.parseColor("#EB5757"),
        "Entertainment" to Color.parseColor("#33FFB5"),
        "Miscellaneous" to Color.parseColor("#8B4513")
    )

    fun getColor(category: String): Int {
        return categoryColors[category] ?: Color.parseColor("#000000")
    }
}