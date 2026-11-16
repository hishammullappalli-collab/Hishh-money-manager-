package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.GoldBarDark
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldChipBg
import com.example.ui.theme.GoldMutedBorder
import com.example.ui.theme.LabelWarm
import com.example.ui.theme.MutedStone
import com.example.ui.theme.TrackDark
import com.example.util.CurrencyFormatter

@Composable
fun SavingsGoalCard(
    monthlyGoal: Double,
    savedAmount: Double,
    progressFraction: Float,
    percentInt: Int,
    onSaveGoal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var goalInput by rememberSaveable {
        mutableStateOf(
            if (monthlyGoal % 1.0 == 0.0) monthlyGoal.toLong().toString() else monthlyGoal.toString()
        )
    }

    LaunchedEffect(monthlyGoal) {
        val formatted = if (monthlyGoal % 1.0 == 0.0) {
            monthlyGoal.toLong().toString()
        } else {
            monthlyGoal.toString()
        }
        if (goalInput != formatted) {
            goalInput = formatted
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "goal_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(CharcoalPanel)
            .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_target_amount),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = LabelWarm
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = goalInput,
                onValueChange = { next ->
                    if (next.length <= 12 && next.all { it.isDigit() || it == '.' }) {
                        goalInput = next
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSaveGoal(goalInput)
                    }
                ),
                colors = luxuryFieldColors(),
                shape = RoundedCornerShape(11.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("goal_amount_input")
            )

            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .height(54.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(GoldChipBg)
                    .border(1.dp, GoldMutedBorder, RoundedCornerShape(11.dp))
                    .clickable {
                        focusManager.clearFocus()
                        onSaveGoal(goalInput)
                    }
                    .padding(horizontal = 16.dp)
                    .testTag("save_goal_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.btn_save_goal),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = GoldBright
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${CurrencyFormatter.formatInr(savedAmount)} ${stringResource(R.string.goal_saved_suffix)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedStone
            )
            Text(
                text = "$percentInt%",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldBright,
                modifier = Modifier.testTag("goal_percent_label")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TrackDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GoldBarDark, GoldBright)
                        )
                    )
            )
        }
    }
}
