package com.example.data

import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow

class MoneyRepository(
    private val transactionDao: TransactionDao,
    private val goalPreferences: GoalPreferencesRepository
) {
    val transactionsFlow: Flow<List<TransactionEntity>> = transactionDao.observeAllTransactions()
    val monthlyGoalFlow: Flow<Double> = goalPreferences.monthlyGoalFlow

    suspend fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: String,
        note: String,
        timestampMillis: Long = System.currentTimeMillis()
    ) {
        val cleanCategory = category.trim().ifEmpty {
            TransactionCategoryCatalog.categoriesFor(type).first()
        }
        val cleanNote = note.trim().take(60).ifEmpty { cleanCategory }
        val entity = TransactionEntity(
            type = type.key,
            amount = amount,
            category = cleanCategory,
            note = cleanNote,
            timestampMillis = timestampMillis,
            yearMonth = DateUtils.yearMonthFromMillis(timestampMillis)
        )
        transactionDao.insertTransaction(entity)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun updateMonthlyGoal(amount: Double) {
        goalPreferences.updateMonthlyGoal(amount)
    }

    suspend fun clearAllData() {
        transactionDao.clearAllTransactions()
        goalPreferences.resetMonthlyGoal()
    }

    suspend fun restoreBackup(payload: BackupPayload) {
        transactionDao.clearAllTransactions()
        if (payload.transactions.isNotEmpty()) {
            transactionDao.insertAllTransactions(payload.transactions)
        }
        goalPreferences.updateMonthlyGoal(payload.goal)
    }
}
