package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import com.example.ui.MoneyUiState
import com.example.ui.components.BalanceHeroSection
import com.example.ui.components.CategoryBreakdownCard
import com.example.ui.components.EmptyTransactionsCard
import com.example.ui.components.MonthSelectorRow
import com.example.ui.components.QuickAddTransactionCard
import com.example.ui.components.SavingsGoalCard
import com.example.ui.components.TransactionRow
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.GoldBright
import com.example.ui.theme.MutedStone
import com.example.ui.theme.WarmIvory

@Composable
fun OverviewScreen(
    state: MoneyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetCurrentMonth: () -> Unit,
    onSubmitTransaction: (
        type: TransactionType,
        amountText: String,
        category: String,
        note: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onSaveGoal: (String) -> Unit,
    onRequestDelete: (TransactionEntity) -> Unit,
    onViewAllTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recentPreview = state.monthTransactions.take(6)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MonthSelectorRow(
                title = stringResource(R.string.section_overview),
                monthLabel = state.monthLabel,
                isCurrentMonth = state.isCurrentMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onResetCurrentMonth = onResetCurrentMonth
            )
        }

        item {
            BalanceHeroSection(
                monthBalance = state.monthBalance,
                monthIncome = state.monthIncome,
                monthExpense = state.monthExpense
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeading(
                title = stringResource(R.string.section_add_transaction),
                subtitle = stringResource(R.string.saved_on_device)
            )
        }

        item {
            QuickAddTransactionCard(onSubmitTransaction = onSubmitTransaction)
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeading(
                title = stringResource(R.string.section_savings_goal),
                subtitle = stringResource(R.string.monthly_target)
            )
        }

        item {
            SavingsGoalCard(
                monthlyGoal = state.monthlyGoal,
                savedAmount = state.goalSavedAmount,
                progressFraction = state.goalProgressFraction,
                percentInt = state.goalPercentInt,
                onSaveGoal = onSaveGoal
            )
        }

        if (state.expenseBreakdown.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeading(
                    title = stringResource(R.string.section_category_breakdown),
                    subtitle = state.monthLabel
                )
            }

            item {
                CategoryBreakdownCard(summaries = state.expenseBreakdown)
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val countLabel = if (state.monthTransactions.size == 1) {
                    "1 ${stringResource(R.string.entry_singular)}"
                } else {
                    "${state.monthTransactions.size} ${stringResource(R.string.entries_suffix)}"
                }
                Column {
                    Text(
                        text = stringResource(R.string.section_recent_transactions),
                        style = MaterialTheme.typography.titleMedium,
                        color = WarmIvory
                    )
                    Text(
                        text = countLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedStone
                    )
                }

                if (state.monthTransactions.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.view_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldBright,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onViewAllTransactions)
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }

        item {
            if (recentPreview.isEmpty()) {
                EmptyTransactionsCard(
                    title = stringResource(R.string.empty_transactions_title),
                    subtitle = stringResource(R.string.empty_transactions_subtitle)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(17.dp))
                        .background(CharcoalPanel)
                        .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    recentPreview.forEachIndexed { index, tx ->
                        TransactionRow(
                            transaction = tx,
                            onRequestDelete = onRequestDelete
                        )
                        if (index < recentPreview.lastIndex) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color(0xFF29282B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = WarmIvory
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MutedStone
        )
    }
}
