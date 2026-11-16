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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.MoneyUiState
import com.example.ui.components.SavingsGoalCard
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.FooterMuted
import com.example.ui.theme.GoldBright
import com.example.ui.theme.WarmIvory

@Composable
fun VaultScreen(
    state: MoneyUiState,
    onSaveGoal: (String) -> Unit,
    onExportCsv: () -> Unit,
    onBackupJson: () -> Unit,
    onRestoreJson: () -> Unit,
    onRequestClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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

        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeading(
                title = stringResource(R.string.section_data_privacy),
                subtitle = stringResource(R.string.saved_on_device)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(17.dp))
                    .background(CharcoalPanel)
                    .border(1.dp, BorderHairline, RoundedCornerShape(17.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VaultActionButton(
                        label = stringResource(R.string.btn_export_csv),
                        icon = Icons.Rounded.TableChart,
                        contentColor = WarmIvory,
                        onClick = onExportCsv,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_csv_button")
                    )
                    VaultActionButton(
                        label = stringResource(R.string.btn_backup_json),
                        icon = Icons.Rounded.Download,
                        contentColor = WarmIvory,
                        onClick = onBackupJson,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("backup_json_button")
                    )
                }

                VaultActionButton(
                    label = stringResource(R.string.btn_restore_json),
                    icon = Icons.Rounded.UploadFile,
                    contentColor = GoldBright,
                    onClick = onRestoreJson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restore_json_button")
                )

                VaultActionButton(
                    label = stringResource(R.string.btn_clear_all),
                    icon = Icons.Rounded.DeleteForever,
                    contentColor = CoralExpense,
                    onClick = onRequestClearAll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_all_button")
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.footer_version),
                    style = MaterialTheme.typography.labelSmall,
                    color = FooterMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.footer_privacy),
                    style = MaterialTheme.typography.bodySmall,
                    color = FooterMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun VaultActionButton(
    label: String,
    icon: ImageVector,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(11.dp))
            .background(ElevatedCharcoal)
            .border(1.dp, Color(0xFF39383A), RoundedCornerShape(11.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}
