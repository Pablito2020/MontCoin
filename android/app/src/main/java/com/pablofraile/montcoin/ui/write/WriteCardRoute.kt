package com.pablofraile.montcoin.ui.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WriteCardRoute(
    model: WriteCardViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val writeResult by model.writeResult.collectAsStateWithLifecycle(initialValue = null)
    val errorMessage by model.errorMessage.collectAsStateWithLifecycle()
    val users by model.users.collectAsStateWithLifecycle()
    val selectedUser by model.selectedUser.collectAsStateWithLifecycle()
    val isRefreshing by model.isRefreshing.collectAsStateWithLifecycle()
    val isWriting by model.isWriting.collectAsStateWithLifecycle()
    WriteCardScreen(
        errorMessage = errorMessage,
        openDrawer = openDrawer,
        users = users,
        onSelectedUser = model::selectUser,
        onOkError = model::clearErrorMessage,
        snackbarHostState = snackbarHostState,
        selectedUser=selectedUser,
        onRefresh = model::updateUsers,
        isRefreshing = isRefreshing,
        writing = isWriting,
        writeResult = writeResult,
        executeWrite = model::startWriting,
        onCancelWrite = model::stopWriting
    )
}