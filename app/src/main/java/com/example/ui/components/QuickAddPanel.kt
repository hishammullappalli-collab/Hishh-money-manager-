package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.TransactionCategoryCatalog
import com.example.data.TransactionType
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldChipBg
import com.example.ui.theme.GoldDeepStart
import com.example.ui.theme.GoldMutedBorder
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.LabelWarm
import com.example.ui.theme.MutedStone
import com.example.ui.theme.WarmIvory

@Composable
fun QuickAddTransactionCard(
    onSubmitTransaction: (
        type: TransactionType,
        amountText: String,
        category: String,
        note: String,
        onSuccess: () -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CharcoalPanel)
            .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
            .padding(16.dp)
    ) {
        TransactionEntryForm(onSubmitTransaction = onSubmitTransaction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onSubmitTransaction: (
        type: TransactionType,
        amountText: String,
        category: String,
        note: String,
        onSuccess: () -> Unit
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CharcoalPanel,
        contentColor = WarmIvory
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.section_add_transaction),
                style = MaterialTheme.typography.titleLarge,
                color = WarmIvory
            )
            Spacer(modifier = Modifier.height(14.dp))
            TransactionEntryForm(
                onSubmitTransaction = { type, amount, cat, note, clearForm ->
                    onSubmitTransaction(type, amount, cat, note) {
                        clearForm()
                        onDismiss()
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionEntryForm(
    onSubmitTransaction: (
        type: TransactionType,
        amountText: String,
        category: String,
        note: String,
        onSuccess: () -> Unit
    ) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var selectedTypeKey by rememberSaveable { mutableStateOf(TransactionType.INCOME.key) }
    val selectedType = TransactionType.fromKey(selectedTypeKey)
    val categories = TransactionCategoryCatalog.categoriesFor(selectedType)
    var selectedCategory by rememberSaveable(selectedTypeKey) {
        mutableStateOf(categories.first())
    }
    var amountText by rememberSaveable { mutableStateOf("") }
    var noteText by rememberSaveable { mutableStateOf("") }

    val submit = {
        focusManager.clearFocus()
        onSubmitTransaction(selectedType, amountText, selectedCategory, noteText) {
            amountText = ""
            noteText = ""
        }
    }

    // Segmented Tabs (+ Income / - Expense)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF101012))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        TypeSegmentTab(
            label = stringResource(R.string.tab_income),
            isIncome = true,
            selected = selectedType == TransactionType.INCOME,
            onClick = {
                selectedTypeKey = TransactionType.INCOME.key
                selectedCategory = TransactionCategoryCatalog.incomeCategories.first()
            },
            modifier = Modifier
                .weight(1f)
                .testTag("income_tab_button")
        )
        TypeSegmentTab(
            label = stringResource(R.string.tab_expense),
            isIncome = false,
            selected = selectedType == TransactionType.EXPENSE,
            onClick = {
                selectedTypeKey = TransactionType.EXPENSE.key
                selectedCategory = TransactionCategoryCatalog.expenseCategories.first()
            },
            modifier = Modifier
                .weight(1f)
                .testTag("expense_tab_button")
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = stringResource(R.string.label_amount),
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        color = LabelWarm
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = amountText,
        onValueChange = { next ->
            if (next.length <= 12 && next.all { it.isDigit() || it == '.' }) {
                amountText = next
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.hint_amount),
                color = MutedStone
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        colors = luxuryFieldColors(),
        shape = RoundedCornerShape(11.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("amount_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = stringResource(R.string.label_category),
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        color = LabelWarm
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isSelected) GoldChipBg else InputBg)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) GoldMutedBorder else InputBorder,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable { selectedCategory = category }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) GoldBright else WarmIvory
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = stringResource(R.string.label_note),
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        color = LabelWarm
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = noteText,
        onValueChange = { if (it.length <= 60) noteText = it },
        placeholder = {
            Text(
                text = stringResource(R.string.hint_note),
                color = MutedStone
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        colors = luxuryFieldColors(),
        shape = RoundedCornerShape(11.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("note_input")
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Primary Champagne Gold CTA
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(GoldDeepStart, GoldBright)
                )
            )
            .clickable(onClick = submit)
            .testTag("add_transaction_button"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.btn_add_transaction),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            ),
            color = Color(0xFF17130C)
        )
    }
}

@Composable
private fun TypeSegmentTab(
    label: String,
    isIncome: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) GoldChipBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isIncome) Icons.Rounded.Add else Icons.Rounded.Remove,
            contentDescription = null,
            tint = if (selected) GoldBright else MutedStone,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (selected) GoldBright else MutedStone
        )
    }
}

@Composable
fun luxuryFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = InputBg,
    unfocusedContainerColor = InputBg,
    focusedBorderColor = Color(0xFFA98543),
    unfocusedBorderColor = InputBorder,
    focusedTextColor = WarmIvory,
    unfocusedTextColor = WarmIvory,
    cursorColor = GoldBright
)
