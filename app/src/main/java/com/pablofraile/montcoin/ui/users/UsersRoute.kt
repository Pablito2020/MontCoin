package com.pablofraile.montcoin.ui.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsersRoute(
    model: UsersViewModel,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val users by model.users.collectAsStateWithLifecycle()
    val errorMessage by model.errors.collectAsStateWithLifecycle(null)
    val isLoadingUsers by model.isLoadingUsers.collectAsStateWithLifecycle()
    val order by model.order.collectAsStateWithLifecycle()
    UsersScreen(
        users = users,
        onRefresh = model::fetchUsers,
        errorMessage = errorMessage,
        currentOrder = order,
        onChangeOrder = model::setOrder,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        isLoading = isLoadingUsers
    )
}