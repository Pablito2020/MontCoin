package com.pablofraile.montcoin.ui.user

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserRoute(
    model: UserViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val isLoadingUser by model.isInitialLoading.collectAsStateWithLifecycle()
    val user by model.user.collectAsStateWithLifecycle()
    val errorMessage by model.errorMessage.collectAsStateWithLifecycle(null)
    val operations by model.operations.collectAsStateWithLifecycle()
    UserScreen(isInitialLoading = isLoadingUser, user = user, operations = operations)
}

