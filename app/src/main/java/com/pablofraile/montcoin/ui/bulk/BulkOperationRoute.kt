package com.pablofraile.montcoin.ui.bulk

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun BulkOperationRoute(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Text("BulkOperationRoute")
}
