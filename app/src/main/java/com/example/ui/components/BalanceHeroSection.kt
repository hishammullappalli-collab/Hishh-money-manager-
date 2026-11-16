package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeepBorder
import com.example.ui.theme.GoldHeroMid
import com.example.ui.theme.GoldHeroTop
import com.example.ui.theme.MutedStone
import com.example.ui.theme.SubtleWarm
import com.example.ui.theme.WarmIvory
import com.example.util.CurrencyFormatter

@Composable
fun MonthSelectorRow(
    title: String,
    monthLabel: String,
    isCurrentMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetCurrentMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = WarmIvory
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(CharcoalPanel)
                .border(1.dp, BorderHairline, RoundedCornerShape(24.dp))
                .padding(horizontal = 4.dp)
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.cd_previous_month),
                    tint = MutedStone
                )
            }

            Text(
                text = monthLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isCurrentMonth) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = if (isCurrentMonth) SubtleWarm else GoldBright,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !isCurrentMonth, onClick = onResetCurrentMonth)
                    .padding(horizontal = 6.dp, vertical = 8.dp)
            )

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.cd_next_month),
                    tint = MutedStone
                )
            }
        }
    }
}

@Composable
fun BalanceHeroSection(
    monthBalance: Double,
    monthIncome: Double,
    monthExpense: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Hero Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(GoldHeroTop, GoldHeroMid, ElevatedCharcoal)
                    )
                )
                .border(1.dp, GoldDeepBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.available_balance).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedStone
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = CurrencyFormatter.formatInr(monthBalance),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = WarmIvory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("available_balance_value")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.income_minus_expenses),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = SubtleWarm
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Side-by-Side Income & Expenses Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = stringResource(R.string.total_income),
                amount = monthIncome,
                accentColor = EmeraldIncome,
                isIncome = true,
                testTag = "total_income_value",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.total_expenses),
                amount = monthExpense,
                accentColor = CoralExpense,
                isIncome = false,
                testTag = "total_expenses_value",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    amount: Double,
    accentColor: Color,
    isIncome: Boolean,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(CharcoalPanel)
            .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
            .padding(15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                color = MutedStone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = if (isIncome) {
                    Icons.AutoMirrored.Rounded.TrendingUp
                } else {
                    Icons.AutoMirrored.Rounded.TrendingDown
                },
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = CurrencyFormatter.formatInr(amount),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = accentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag(testTag)
        )
    }
}
