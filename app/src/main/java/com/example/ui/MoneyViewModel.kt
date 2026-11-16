package com.example.ui

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AppDatabase
import com.example.data.BackupCodec
import com.example.data.CategorySummary
import com.example.data.GoalPreferencesRepository
import com.example.data.MoneyRepository
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import com.example.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

enum class TransactionFilter {
    ALL,
    INCOME,
    EXPENSE
}

data class MoneyUiState(
    val selectedYearMonth: String = DateUtils.currentYearMonth(),
    val monthLabel: String = DateUtils.formatMonthLabel(DateUtils.currentYearMonth()),
    val isCurrentMonth: Boolean = true,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val monthBalance: Double = 0.0,
    val monthlyGoal: Double = GoalPreferencesRepository.DEFAULT_MONTHLY_GOAL,
    val goalSavedAmount: Double = 0.0,
    val goalProgressFraction: Float = 0f,
    val goalPercentInt: Int = 0,
    val monthTransactions: List<TransactionEntity> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredHistoryTransactions: List<TransactionEntity> = emptyList(),
    val historyFilterType: TransactionFilter = TransactionFilter.ALL,
    val searchQuery: String = "",
    val expenseBreakdown: List<CategorySummary> = emptyList(),
    val incomeBreakdown: List<CategorySummary> = emptyList(),
    val isLoading: Boolean = true
)

data class UiFeedback(
    @StringRes val messageResId: Int
)

class MoneyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoneyRepository by lazy {
        val db = AppDatabase.getInstance(application)
        MoneyRepository(
            transactionDao = db.transactionDao(),
            goalPreferences = GoalPreferencesRepository(application)
        )
    }

    private val selectedMonthFlow = MutableStateFlow(DateUtils.currentYearMonth())
    private val filterTypeFlow = MutableStateFlow(TransactionFilter.ALL)
    private val searchQueryFlow = MutableStateFlow("")

    private val _feedbackEvents = MutableSharedFlow<UiFeedback>(extraBufferCapacity = 4)
    val feedbackEvents: SharedFlow<UiFeedback> = _feedbackEvents.asSharedFlow()

    val uiState: StateFlow<MoneyUiState> = combine(
        repository.transactionsFlow,
        repository.monthlyGoalFlow,
        selectedMonthFlow,
        filterTypeFlow,
        searchQueryFlow
    ) { allTx, goal, yearMonth, filterType, query ->
        val monthTx = allTx.filter { it.yearMonth == yearMonth }
        val income = monthTx
            .filter { it.transactionType == TransactionType.INCOME }
            .sumOf { it.amount }
        val expense = monthTx
            .filter { it.transactionType == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val net = income - expense
        val saved = max(0.0, net)
        val fraction = if (goal > 0.0) (saved / goal).toFloat().coerceIn(0f, 1f) else 0f
        val percentInt = (fraction * 100f).roundToInt()

        val trimmedQuery = query.trim().lowercase()
        val filteredHistory = monthTx.filter { tx ->
            val typeMatch = when (filterType) {
                TransactionFilter.ALL -> true
                TransactionFilter.INCOME -> tx.transactionType == TransactionType.INCOME
                TransactionFilter.EXPENSE -> tx.transactionType == TransactionType.EXPENSE
            }
            val queryMatch = trimmedQuery.isEmpty() ||
                tx.note.lowercase().contains(trimmedQuery) ||
                tx.category.lowercase().contains(trimmedQuery)
            typeMatch && queryMatch
        }

        val expBreakdown = buildBreakdown(
            transactions = monthTx.filter { it.transactionType == TransactionType.EXPENSE },
            type = TransactionType.EXPENSE,
            total = expense
        )
        val incBreakdown = buildBreakdown(
            transactions = monthTx.filter { it.transactionType == TransactionType.INCOME },
            type = TransactionType.INCOME,
            total = income
        )

        MoneyUiState(
            selectedYearMonth = yearMonth,
            monthLabel = DateUtils.formatMonthLabel(yearMonth),
            isCurrentMonth = yearMonth == DateUtils.currentYearMonth(),
            monthIncome = income,
            monthExpense = expense,
            monthBalance = net,
            monthlyGoal = goal,
            goalSavedAmount = saved,
            goalProgressFraction = fraction,
            goalPercentInt = percentInt,
            monthTransactions = monthTx,
            allTransactions = allTx,
            filteredHistoryTransactions = filteredHistory,
            historyFilterType = filterType,
            searchQuery = query,
            expenseBreakdown = expBreakdown,
            incomeBreakdown = incBreakdown,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoneyUiState()
    )

    fun selectPreviousMonth() {
        selectedMonthFlow.value = DateUtils.shiftMonth(selectedMonthFlow.value, -1)
    }

    fun selectNextMonth() {
        selectedMonthFlow.value = DateUtils.shiftMonth(selectedMonthFlow.value, 1)
    }

    fun resetToCurrentMonth() {
        selectedMonthFlow.value = DateUtils.currentYearMonth()
    }

    fun setHistoryFilter(filter: TransactionFilter) {
        filterTypeFlow.value = filter
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun addTransaction(
        type: TransactionType,
        rawAmount: String,
        category: String,
        note: String,
        onSuccess: () -> Unit = {}
    ) {
        val parsed = rawAmount.trim().replace(",", "").toDoubleOrNull()
        if (parsed == null || !parsed.isFinite() || parsed <= 0.0) {
            _feedbackEvents.tryEmit(UiFeedback(R.string.msg_invalid_amount))
            return
        }
        viewModelScope.launch {
            repository.addTransaction(
                type = type,
                amount = parsed,
                category = category,
                note = note
            )
            selectedMonthFlow.value = DateUtils.currentYearMonth()
            _feedbackEvents.emit(UiFeedback(R.string.msg_transaction_saved))
            onSuccess()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _feedbackEvents.emit(UiFeedback(R.string.msg_transaction_deleted))
        }
    }

    fun updateGoal(rawGoal: String) {
        val parsed = rawGoal.trim().replace(",", "").toDoubleOrNull()
        if (parsed == null || !parsed.isFinite() || parsed < 0.0) {
            _feedbackEvents.tryEmit(UiFeedback(R.string.msg_invalid_amount))
            return
        }
        viewModelScope.launch {
            repository.updateMonthlyGoal(parsed)
            _feedbackEvents.emit(UiFeedback(R.string.msg_goal_updated))
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _feedbackEvents.emit(UiFeedback(R.string.msg_all_cleared))
        }
    }

    fun buildCsvExport(): String? {
        val list = uiState.value.allTransactions
        if (list.isEmpty()) {
            _feedbackEvents.tryEmit(UiFeedback(R.string.msg_no_export))
            return null
        }
        return BackupCodec.encodeCsv(list)
    }

    fun buildJsonBackup(): String {
        val state = uiState.value
        return BackupCodec.encodeJson(state.allTransactions, state.monthlyGoal)
    }

    fun notifyExportSuccess() {
        _feedbackEvents.tryEmit(UiFeedback(R.string.msg_export_ready))
    }

    fun restoreJsonBackup(rawJson: String) {
        viewModelScope.launch {
            val payload = withContext(Dispatchers.Default) {
                BackupCodec.decodeJson(rawJson)
            }
            if (payload == null) {
                _feedbackEvents.emit(UiFeedback(R.string.msg_backup_invalid))
                return@launch
            }
            repository.restoreBackup(payload)
            selectedMonthFlow.value = DateUtils.currentYearMonth()
            _feedbackEvents.emit(UiFeedback(R.string.msg_backup_restored))
        }
    }

    private fun buildBreakdown(
        transactions: List<TransactionEntity>,
        type: TransactionType,
        total: Double
    ): List<CategorySummary> {
        if (transactions.isEmpty() || total <= 0.0) return emptyList()
        return transactions
            .groupBy { it.category }
            .map { (cat, items) ->
                val sum = items.sumOf { it.amount }
                CategorySummary(
                    category = cat,
                    type = type,
                    totalAmount = sum,
                    fraction = (sum / total).toFloat().coerceIn(0f, 1f)
                )
            }
            .sortedByDescending { it.totalAmount }
    }
}
