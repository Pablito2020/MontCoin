package com.pablofraile.montcoin.ui.write

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.SearchingAnimation
import com.pablofraile.montcoin.ui.common.UserChip
import com.pablofraile.montcoin.ui.common.UserSelectable
import com.pablofraile.montcoin.ui.operation.ErrorOperationDialog
import kotlinx.coroutines.launch
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteCardScreen(
    writing: Boolean,
    writeResult: Pair<User, Date>? = null,
    users: List<User> = emptyList(),
    selectedUser: User? = null,
    errorMessage: String? = null,
    isRefreshing: Boolean = false,
    onSelectedUser: (User) -> Unit = {},
    executeWrite: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onOkError: () -> Unit = {},
    onCancelWrite: () -> Unit = {},
    openDrawer: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
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
                    if (writing) return@CenterAlignedTopAppBar
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
            writing = writing,
            executeWrite = executeWrite,
            writeResult = writeResult,
            users = users,
            onSelectedUser = onSelectedUser,
            selectedUser = selectedUser,
            errorMessage = errorMessage,
            onOkError = onOkError,
            onCancelWrite = onCancelWrite,
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
    writing: Boolean,
    executeWrite: () -> Unit,
    writeResult: Pair<User, Date>?,
    errorMessage: String?,
    users: List<User>,
    selectedUser: User?,
    onSelectedUser: (User) -> Unit,
    onCancelWrite: () -> Unit,
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
        if (writing) {
            Text("Searching card to write", style = MaterialTheme.typography.bodyLarge)
            selectedUser?.name?.let {
                UserChip(
                    userName = it,
                    onClose = onCancelWrite,
                    modifier = Modifier.padding(15.dp)
                )
            }
            SearchingAnimation()
        } else {
            val userSelectable = users.map {
                UserSelectable(it, isSelected = it.id == selectedUser?.id)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text("Select the user you want to write to the card:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                UserSelectable(
                    users = userSelectable,
                    onUserClicked = onSelectedUser,
                    modifier = Modifier
                        .weight(0.8f)
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (selectedUser != null) 1.0f else 0f,
                    label = "alpha"
                )
                Button(
                    onClick = executeWrite,
                    modifier = Modifier
                        .padding(16.dp)
                        .graphicsLayer {
                            alpha = animatedAlpha
                        }

                ) {
                    Text("Write to card")
                }
            }
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
        writing = false,
        users = listOf(
            User(Id("1"), "Pablo", Amount(1000)),
            User(Id("2"), "Juan", Amount(2000)),
            User(Id("3"), "Pedro", Amount(3000)),
        ),
        selectedUser = User(Id("1"), "Pablo", Amount(1000)),
    )
}
