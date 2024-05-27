package com.pablofraile.montcoin.ui.write

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WriteCardRoute(
    model: WriteCardViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val sensor by model.sensor.collectAsStateWithLifecycle()
    val writeResult by model.writeResult.collectAsStateWithLifecycle(initialValue = null)
    val errorMessage by model.errorMessage.collectAsStateWithLifecycle()
    WriteCardScreen(
        sensor = sensor,
        writeResult = writeResult,
        onStart = model::startSearching,
        onStop = model::stopSearching,
        openDrawer = openDrawer,
        errorMessage = errorMessage,
        onOkError = model::clearErrorMessage,
        snackbarHostState = snackbarHostState
    )
}
