package net.pablofraile.ui.user

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserRoute(
    model: UserViewModel,
    onGoBack : () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val isLoading by model.isLoading.collectAsStateWithLifecycle()
    val user by model.user.collectAsStateWithLifecycle()
    val errorMessage by model.errorMessage.collectAsStateWithLifecycle(null)
    val operations by model.operations.collectAsStateWithLifecycle()
    val percentage by model.percentage.collectAsStateWithLifecycle()
    UserScreen(
        isLoading = isLoading,
        user = user,
        operations = operations,
        onRefresh = model::onRefresh,
        goBack = onGoBack,
        errorMessage = errorMessage,
        onOkError = onGoBack,
        percentage = percentage,
        onRetryError = {
            model.cleanError()
            model.onRefresh()
        },
        snackbarHostState = snackbarHostState
    )
}

