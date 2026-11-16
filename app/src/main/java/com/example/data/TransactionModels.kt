package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val key: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromKey(key: String): TransactionType =
            if (key.equals("expense", ignoreCase = true)) EXPENSE else INCOME
    }
}

object TransactionCategoryCatalog {
    val incomeCategories = listOf(
        "Business",
        "Freelancing / Editing",
        "Pocket Money",
        "Other Income"
    )

    val expenseCategories = listOf(
        "Personal",
        "Business Expenses",
        "Food",
        "Travel",
        "Shopping",
        "Education",
        "Other Expense"
    )

    fun categoriesFor(type: TransactionType): List<String> =
        if (type == TransactionType.INCOME) incomeCategories else expenseCategories
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String,
    val amount: Double,
    val category: String,
    val note: String,
    val timestampMillis: Long,
    val yearMonth: String
) {
    val transactionType: TransactionType
        get() = TransactionType.fromKey(type)
}

data class CategorySummary(
    val category: String,
    val type: TransactionType,
    val totalAmount: Double,
    val fraction: Float
)
