package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.TransactionEntity
import com.example.ui.MoneyUiState
import com.example.ui.TransactionFilter
import com.example.ui.components.EmptyTransactionsCard
import com.example.ui.components.MonthSelectorRow
import com.example.ui.components.TransactionRow
import com.example.ui.components.luxuryFieldColors
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldChipBg
import com.example.ui.theme.GoldMutedBorder
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.MutedStone
import com.example.ui.theme.WarmIvory

@Composable
fun HistoryScreen(
    state: MoneyUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetCurrentMonth: () -> Unit,
    onFilterChange: (TransactionFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRequestDelete: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MonthSelectorRow(
                title = stringResource(R.string.nav_history),
                monthLabel = state.monthLabel,
                isCurrentMonth = state.isCurrentMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onResetCurrentMonth = onResetCurrentMonth
            )
        }

        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_transactions_hint),
                        color = MutedStone
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MutedStone
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.cd_clear_search),
                                tint = MutedStone
                            )
                        }
                    }
                },
                singleLine = true,
                colors = luxuryFieldColors(),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipPill(
                    label = stringResource(R.string.filter_all),
                    selected = state.historyFilterType == TransactionFilter.ALL,
                    onClick = { onFilterChange(TransactionFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
                FilterChipPill(
                    label = stringResource(R.string.tab_income),
                    selected = state.historyFilterType == TransactionFilter.INCOME,
                    onClick = { onFilterChange(TransactionFilter.INCOME) },
                    modifier = Modifier.weight(1f)
                )
                FilterChipPill(
                    label = stringResource(R.string.tab_expense),
                    selected = state.historyFilterType == TransactionFilter.EXPENSE,
                    onClick = { onFilterChange(TransactionFilter.EXPENSE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.filteredHistoryTransactions.isEmpty()) {
            item {
                val isFiltered = state.searchQuery.isNotBlank() ||
                    state.historyFilterType != TransactionFilter.ALL
                EmptyTransactionsCard(
                    title = stringResource(R.string.empty_transactions_title),
                    subtitle = stringResource(
                        if (isFiltered) {
                            R.string.empty_filtered_subtitle
                        } else {
                            R.string.empty_transactions_subtitle
                        }
                    )
                )
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(17.dp))
                        .background(CharcoalPanel)
                        .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    state.filteredHistoryTransactions.forEachIndexed { index, tx ->
                        TransactionRow(
                            transaction = tx,
                            onRequestDelete = onRequestDelete
                        )
                        if (index < state.filteredHistoryTransactions.lastIndex) {
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
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) GoldChipBg else InputBg)
            .border(
                width = 1.dp,
                color = if (selected) GoldMutedBorder else InputBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) GoldBright else WarmIvory
        )
    }
}
