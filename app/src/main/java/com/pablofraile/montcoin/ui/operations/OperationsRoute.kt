package com.pablofraile.montcoin.ui.operations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationsRoute(
    model: OperationsViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val transactions by model.operations.collectAsStateWithLifecycle()
    val errorMessage by model.errorMessages.collectAsStateWithLifecycle()
    val isLoading by model.initialLoading.collectAsStateWithLifecycle()
    OperationsScreen(
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        operations = transactions,
        isLoading = isLoading,
        onRefresh = model::reload,
        onReload = model::fetchFirstPage,
        errorMessage = errorMessage,
        loadMoreItems = {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                model.fetchNextPage()
            }
        },
    )
}