package com.pablofraile.montcoin.ui.operation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pablofraile.montcoin.model.isValidAmount

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationRoute(
    model: OperationViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle){
        onDispose {
            model.stopSearchingDevices()
        }
    }
    val card by model.sensor.collectAsStateWithLifecycle()
    val amount by model.amount.collectAsStateWithLifecycle()
    val operation by model.operationResult.collectAsStateWithLifecycle(initialValue = null)
    val isDoingOperation by model.isDoingOperation.collectAsStateWithLifecycle()
    val errorMessage by model.errorMessage.collectAsStateWithLifecycle()
    val modelProducer = model.modelProducer
    OperationScreen(
        amount = amount,
        amountIsValid = amount.isValidAmount(),
        card = card,
        modelProducer = modelProducer,
        onStart = model::searchDevices,
        onStop = model::stopSearchingDevices,
        onAmountChange = model::changeAmount,
        isDoingOperation = isDoingOperation,
        errorMessage = errorMessage,
        closeError = model::cleanError,
        operation = operation,
        openDrawer = openDrawer,
        onRefresh = model::onRefresh    ,
        snackbarHostState = snackbarHostState
    )
}