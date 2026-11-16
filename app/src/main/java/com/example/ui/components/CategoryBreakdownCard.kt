package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.CategorySummary
import com.example.data.TransactionType
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.MutedStone
import com.example.ui.theme.TrackDark
import com.example.ui.theme.WarmIvory
import com.example.util.CurrencyFormatter
import kotlin.math.roundToInt

@Composable
fun CategoryBreakdownCard(
    summaries: List<CategorySummary>,
    modifier: Modifier = Modifier
) {
    if (summaries.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CharcoalPanel)
            .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        summaries.take(5).forEach { item ->
            val accent = if (item.type == TransactionType.INCOME) EmeraldIncome else CoralExpense
            val pct = (item.fraction * 100f).roundToInt()
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = WarmIvory,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${CurrencyFormatter.formatInr(item.totalAmount)} · $pct%",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MutedStone
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrackDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(item.fraction.coerceAtLeast(0.03f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(accent)
                    )
                }
            }
        }
    }
}
