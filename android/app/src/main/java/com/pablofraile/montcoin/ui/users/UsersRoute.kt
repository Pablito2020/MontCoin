package com.pablofraile.montcoin.ui.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pablofraile.montcoin.model.User

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsersRoute(
    model: UsersViewModel,
    onUserClick: (User) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val users by model.users.collectAsStateWithLifecycle()
    val errorMessage by model.errors.collectAsStateWithLifecycle(null)
    val isLoadingUsers by model.isLoadingUsers.collectAsStateWithLifecycle()
    val order by model.order.collectAsStateWithLifecycle()
    val search by model.search.collectAsStateWithLifecycle()
    UsersScreen(
        users = users,
        onRefresh = model::fetchUsers,
        errorMessage = errorMessage,
        currentOrder = order,
        search = search,
        onClick = onUserClick,
        onSearchChange = model::setSearch,
        onChangeOrder = model::setOrder,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        isLoading = isLoadingUsers,
        fetchUsersFirstTime = model::fetchUsersFirstTime
    )
}