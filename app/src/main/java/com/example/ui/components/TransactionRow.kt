package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldBright
import com.example.ui.theme.MutedStone
import com.example.ui.theme.WarmIvory
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    onRequestDelete: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.transactionType == TransactionType.INCOME
    val amountColor = if (isIncome) EmeraldIncome else CoralExpense

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF24221D)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(transaction.category, isIncome),
                contentDescription = transaction.category,
                tint = GoldBright,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.note,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = WarmIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${transaction.category} · ${DateUtils.formatTransactionDate(transaction.timestampMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedStone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = CurrencyFormatter.formatSignedInr(transaction.amount, isIncome),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = amountColor,
            maxLines = 1
        )

        IconButton(
            onClick = { onRequestDelete(transaction) },
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("delete_tx_${transaction.id}")
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = stringResource(R.string.cd_delete_transaction),
                tint = MutedStone,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmptyTransactionsCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CharcoalPanel)
            .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF24221D)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountBalanceWallet,
                contentDescription = null,
                tint = GoldBright,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = WarmIvory,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedStone,
            textAlign = TextAlign.Center
        )
    }
}

private fun categoryIcon(category: String, isIncome: Boolean): ImageVector {
    return when (category) {
        "Business" -> Icons.Rounded.Work
        "Freelancing / Editing" -> Icons.Rounded.Brush
        "Pocket Money" -> Icons.Rounded.Savings
        "Other Income" -> Icons.Rounded.Paid
        "Personal" -> Icons.Rounded.Person
        "Business Expenses" -> Icons.Rounded.BusinessCenter
        "Food" -> Icons.Rounded.Restaurant
        "Travel" -> Icons.Rounded.Flight
        "Shopping" -> Icons.Rounded.ShoppingBag
        "Education" -> Icons.Rounded.School
        "Other Expense" -> Icons.Rounded.ReceiptLong
        else -> if (isIncome) Icons.Rounded.Paid else Icons.Rounded.ReceiptLong
    }
}
