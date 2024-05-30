package com.pablofraile.montcoin.ui.write

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.Autocomplete
import com.pablofraile.montcoin.ui.common.LoadingAnimation
import com.pablofraile.montcoin.ui.common.UserChip
import com.pablofraile.montcoin.ui.operation.ErrorOperationDialog
import kotlinx.coroutines.launch
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteCardScreen(
    writeResult: Pair<User, Date>? = null,
    users: List<User> = emptyList(),
    selectedUser: User? = null,
    errorMessage: String? = null,
    onSelectedUser: (User) -> Unit = {},
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false,
    onOkError: () -> Unit = {},
    onClearUser: () -> Unit = {},
    openDrawer: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Write Card",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRefresh
                    ) {
                        if (isRefreshing) {
                            val state = PullToRefreshState(
                                positionalThresholdPx = 0.toFloat(),
                                initialRefreshing = true,
                                enabled = { true }
                            )
                            PullToRefreshDefaults.Indicator(state = state)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh list of users"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        WriteCardContent(
            writeResult = writeResult,
            users = users,
            onSelectedUser = onSelectedUser,
            selectedUser = selectedUser,
            errorMessage = errorMessage,
            onOkError = onOkError,
            onClearUser = onClearUser,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    }
}


@Composable
fun ShowSnackBar(
    user: Pair<User, Date>,
    snackbarHostState: SnackbarHostState,
    onClosedSnackBar: () -> Unit = {}
) {
    LaunchedEffect(key1 = user.second) {
        launch {
            snackbarHostState.showSnackbar("Written card for ${user.first.name}")
            onClosedSnackBar()
        }
    }
}


@Composable
fun WriteCardContent(
    writeResult: Pair<User, Date>?,
    errorMessage: String?,
    users: List<User>,
    selectedUser: User?,
    onSelectedUser: (User) -> Unit,
    onClearUser: () -> Unit,
    onOkError: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (selectedUser == null) {
            Autocomplete(
                label = "Search User",
                items = users,
                modifier = modifier,
                onItemSelected = onSelectedUser,
                toString = { it.name })
        } else {
            Text("Searching card to write", style = MaterialTheme.typography.bodyLarge)
            UserChip(
                userName = selectedUser.name,
                onClose = onClearUser,
                modifier = Modifier.padding(15.dp)
            )
            LoadingAnimation()
        }
    }
    if (writeResult != null)
        ShowSnackBar(
            user = writeResult,
            snackbarHostState = snackbarHostState
        )
    if (errorMessage != null)
        ErrorOperationDialog(message = errorMessage, onOk = onOkError)
}

@Preview(showBackground = true)
@Composable
fun WriteCardPreview() {
    WriteCardScreen(
        selectedUser = User(Id("1"), "Pablo", Amount(1000)),
    )
}
