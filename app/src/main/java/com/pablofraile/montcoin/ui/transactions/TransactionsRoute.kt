package com.pablofraile.montcoin.ui.transactions

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransactionsRoute(
    model: TransactionsViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val transactions by model.transactions.collectAsStateWithLifecycle()
    TransactionsScreen(
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        transactions = transactions,
        onRefresh = model::refreshTransactions,
    )
}