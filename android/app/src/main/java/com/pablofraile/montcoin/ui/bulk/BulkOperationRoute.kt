package com.pablofraile.montcoin.ui.bulk

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.pablofraile.montcoin.model.isValidAmount


@Composable
fun BulkOperationRoute(
    viewModel: BulkOperationViewModel,
    openDrawer: () -> Unit,
    onClose: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val amount by viewModel.amount.collectAsState()
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val correctOperation by viewModel.correctOperation.collectAsState()
    val error by viewModel.errors.collectAsState()
    BulkOperationScreen(
        isLoading = isLoading,
        amount = amount,
        isValidAmount = amount.isValidAmount(),
        users = users,
        onOperationExecute = viewModel::makeOperation,
        onSelectedUser = viewModel::onSelectedUser,
        toggleAllUsers = viewModel::toggleAllUsers,
        onAmountChange = viewModel::changeAmount,
        openDrawer = openDrawer,
        result = correctOperation,
        onResultShowed = viewModel::cleanCorrectOperation,
        error = error,
        onRetryError = viewModel::cleanErrors,
        onCloseError = onClose,
        snackbarHostState = snackbarHostState
    )
}
