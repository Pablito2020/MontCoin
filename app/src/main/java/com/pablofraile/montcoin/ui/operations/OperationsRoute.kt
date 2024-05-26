package com.pablofraile.montcoin.ui.operations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationsRoute(
    model: OperationsViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val transactions by model.operations.collectAsStateWithLifecycle()
    OperationsScreen(
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        operations = transactions,
        onRefresh = model::refreshOperations,
        loadMoreItems = model::loadMoreOperations,
    )
}