package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.TransactionEntity
import com.example.ui.components.AddTransactionBottomSheet
import com.example.ui.components.BrandHeader
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.BorderHairline
import com.example.ui.theme.CharcoalPanel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldChipBg
import com.example.ui.theme.GoldMutedBorder
import com.example.ui.theme.MutedStone
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmIvory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MainDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    OVERVIEW(
        route = "overview",
        labelRes = R.string.nav_overview,
        selectedIcon = Icons.Rounded.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    ),
    HISTORY(
        route = "history",
        labelRes = R.string.nav_history,
        selectedIcon = Icons.Rounded.ReceiptLong,
        unselectedIcon = Icons.Outlined.ReceiptLong
    ),
    VAULT(
        route = "vault",
        labelRes = R.string.nav_vault,
        selectedIcon = Icons.Rounded.Security,
        unselectedIcon = Icons.Outlined.Security
    )
}

@Composable
fun HishhMoneyApp(
    viewModel: MoneyViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentTabName by rememberSaveable { mutableStateOf(MainDestination.OVERVIEW.name) }
    val currentTab = MainDestination.valueOf(currentTabName)

    var showQuickAddSheet by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var showClearAllDialog by rememberSaveable { mutableStateOf(false) }

    var pendingCsvContent by remember { mutableStateOf<String?>(null) }
    var pendingJsonContent by remember { mutableStateOf<String?>(null) }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        val csv = pendingCsvContent
        pendingCsvContent = null
        if (uri != null && csv != null) {
            scope.launch {
                val ok = writeTextToUri(context, uri, csv)
                if (ok) viewModel.notifyExportSuccess()
            }
        }
    }

    val jsonBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        val json = pendingJsonContent
        pendingJsonContent = null
        if (uri != null && json != null) {
            scope.launch {
                val ok = writeTextToUri(context, uri, json)
                if (ok) viewModel.notifyExportSuccess()
            }
        }
    }

    val jsonRestoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val text = readTextFromUri(context, uri)
                if (text != null) {
                    viewModel.restoreJsonBackup(text)
                } else {
                    snackbarHostState.showSnackbar(context.getString(R.string.msg_backup_invalid))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.feedbackEvents.collect { feedback ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(context.getString(feedback.messageResId))
        }
    }

    if (currentTab != MainDestination.OVERVIEW) {
        BackHandler {
            currentTabName = MainDestination.OVERVIEW.name
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = ObsidianBg,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                BrandHeader()
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = CharcoalPanel,
                        contentColor = WarmIvory,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        MainDestination.entries.forEach { destination ->
                            val selected = destination == currentTab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTabName = destination.name },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = stringResource(destination.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(destination.labelRes),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GoldBright,
                                    selectedTextColor = GoldBright,
                                    indicatorColor = GoldChipBg,
                                    unselectedIconColor = MutedStone,
                                    unselectedTextColor = MutedStone
                                ),
                                modifier = Modifier.testTag("nav_${destination.route}")
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (currentTab != MainDestination.VAULT) {
                    FloatingActionButton(
                        onClick = { showQuickAddSheet = true },
                        containerColor = GoldBright,
                        contentColor = Color(0xFF17130C),
                        modifier = Modifier.testTag("fab_quick_add")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.cd_quick_add)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        containerColor = Color(0xFF29251D),
                        contentColor = Color(0xFFF3D99F),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .border(1.dp, GoldMutedBorder, RoundedCornerShape(30.dp))
                    ) {
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = CharcoalPanel,
                        contentColor = WarmIvory,
                        modifier = Modifier
                            .fillMaxHeight()
                            .border(1.dp, BorderHairline)
                    ) {
                        MainDestination.entries.forEach { destination ->
                            val selected = destination == currentTab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTabName = destination.name },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = stringResource(destination.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(destination.labelRes),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = GoldBright,
                                    selectedTextColor = GoldBright,
                                    indicatorColor = GoldChipBg,
                                    unselectedIconColor = MutedStone,
                                    unselectedTextColor = MutedStone
                                ),
                                modifier = Modifier.testTag("rail_${destination.route}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 720.dp)
                    ) {
                        when (currentTab) {
                            MainDestination.OVERVIEW -> {
                                OverviewScreen(
                                    state = state,
                                    onPreviousMonth = viewModel::selectPreviousMonth,
                                    onNextMonth = viewModel::selectNextMonth,
                                    onResetCurrentMonth = viewModel::resetToCurrentMonth,
                                    onSubmitTransaction = viewModel::addTransaction,
                                    onSaveGoal = viewModel::updateGoal,
                                    onRequestDelete = { pendingDeleteTx = it },
                                    onViewAllTransactions = {
                                        currentTabName = MainDestination.HISTORY.name
                                    }
                                )
                            }

                            MainDestination.HISTORY -> {
                                HistoryScreen(
                                    state = state,
                                    onPreviousMonth = viewModel::selectPreviousMonth,
                                    onNextMonth = viewModel::selectNextMonth,
                                    onResetCurrentMonth = viewModel::resetToCurrentMonth,
                                    onFilterChange = viewModel::setHistoryFilter,
                                    onSearchQueryChange = viewModel::setSearchQuery,
                                    onRequestDelete = { pendingDeleteTx = it }
                                )
                            }

                            MainDestination.VAULT -> {
                                VaultScreen(
                                    state = state,
                                    onSaveGoal = viewModel::updateGoal,
                                    onExportCsv = {
                                        val csv = viewModel.buildCsvExport()
                                        if (csv != null) {
                                            pendingCsvContent = csv
                                            csvExportLauncher.launch("hishh-money-transactions.csv")
                                        }
                                    },
                                    onBackupJson = {
                                        pendingJsonContent = viewModel.buildJsonBackup()
                                        jsonBackupLauncher.launch("hishh-money-backup.json")
                                    },
                                    onRestoreJson = {
                                        jsonRestoreLauncher.launch(
                                            arrayOf("application/json", "text/plain")
                                        )
                                    },
                                    onRequestClearAll = { showClearAllDialog = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQuickAddSheet) {
        AddTransactionBottomSheet(
            onDismiss = { showQuickAddSheet = false },
            onSubmitTransaction = viewModel::addTransaction
        )
    }

    pendingDeleteTx?.let { target ->
        AlertDialog(
            onDismissRequest = { pendingDeleteTx = null },
            containerColor = CharcoalPanel,
            titleContentColor = WarmIvory,
            textContentColor = MutedStone,
            title = { Text(stringResource(R.string.dialog_delete_title)) },
            text = { Text(stringResource(R.string.dialog_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(target.id)
                        pendingDeleteTx = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = CoralExpense,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTx = null }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = WarmIvory
                    )
                }
            }
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            containerColor = CharcoalPanel,
            titleContentColor = WarmIvory,
            textContentColor = MutedStone,
            title = { Text(stringResource(R.string.dialog_clear_title)) },
            text = { Text(stringResource(R.string.dialog_clear_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearAllDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_clear),
                        color = CoralExpense,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = WarmIvory
                    )
                }
            }
        )
    }
}

private suspend fun writeTextToUri(context: Context, uri: Uri, content: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray(Charsets.UTF_8))
                stream.flush()
                true
            } ?: false
        } catch (_: Exception) {
            false
        }
    }
}

private suspend fun readTextFromUri(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            }
        } catch (_: Exception) {
            null
        }
    }
}
