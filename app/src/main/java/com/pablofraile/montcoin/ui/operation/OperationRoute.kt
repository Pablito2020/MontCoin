package com.pablofraile.montcoin.ui.operation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationRoute(
    model: OperationViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val card by model.sensor.collectAsStateWithLifecycle()
    val amount by model.amount.collectAsStateWithLifecycle()
    val operation by model.operationResult.collectAsStateWithLifecycle(initialValue = null)
    val isDoingOperation by model.isDoingOperation.collectAsStateWithLifecycle()
    OperationScreen(
        amount = amount.value,
        amountIsValid = amount.isValid(),
        card = card,
        onStart = model::searchDevices,
        onStop = model::stopSearchingDevices,
        onAmountChange = model::changeAmount,
        isDoingOperation = isDoingOperation,
        operation = operation,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState
    )
}