package com.pablofraile.montcoin.ui.operation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationRoute(
    model: OperationViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val card by model.sensor.collectAsStateWithLifecycle()
    val amount by model.amount.collectAsStateWithLifecycle()
    val operation by model.result.collectAsStateWithLifecycle()
    val showResult by model.showOperationResult.collectAsStateWithLifecycle()
    val isDoingOperation by model.isDoingOperation.collectAsStateWithLifecycle()
    OperationScreen(
        amount = amount.value,
        amountIsValid = amount.isValid(),
        card = card,
        operation = operation,
        onStart = model::searchDevices,
        onStop = model::stopSearchingDevices,
        onAmountChange = model::changeAmount,
        isDoingOperation = isDoingOperation,
        showOperationResult = showResult,
        onOperationErrorRead = model::cleanOperationResult,
        onOperationCorrectRead = model::cleanOperationResult,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState
    )
}
