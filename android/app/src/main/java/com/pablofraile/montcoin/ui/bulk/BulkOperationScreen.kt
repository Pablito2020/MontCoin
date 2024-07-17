package com.pablofraile.montcoin.ui.bulk

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Unpublished
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.BulkOperationResult
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.UserSelectable
import com.pablofraile.montcoin.ui.operation.AmountTextBox
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkOperationScreen(
    amount: String,
    isValidAmount: Boolean,
    users: List<UserSelectable>,
    isLoading: Boolean,
    result: BulkOperationResult?,
    error: String?,
    onOperationExecute: () -> Unit = {},
    onResultShowed: () -> Unit = {},
    onSelectedUser: (User) -> Unit = {},
    toggleAllUsers: () -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    openDrawer: () -> Unit = {},
    onRetryError: () -> Unit = {},
    onCloseError: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bulk Operation",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    if (users.isNotEmpty() && !isLoading) {
                        IconButton(onClick = toggleAllUsers) {
                            val icon =
                                if (users.all { it.isSelected }) Icons.Filled.Unpublished else Icons.Filled.Check
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        BulkOperationContent(
            error = error,
            isLoading = isLoading,
            amount = amount,
            isValidAmount = isValidAmount,
            users = users,
            onOperationExecute = onOperationExecute,
            onSelectedUser = onSelectedUser,
            onAmountChange = onAmountChange,
            result = result,
            onResultShowed = onResultShowed,
            snackbarHostState = snackbarHostState,
            onRetryError = onRetryError,
            onCloseError = onCloseError,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

@Composable
fun BulkOperationContent(
    error: String?,
    isLoading: Boolean,
    amount: String,
    isValidAmount: Boolean,
    result: BulkOperationResult?,
    snackbarHostState: SnackbarHostState,
    users: List<UserSelectable>,
    onOperationExecute: () -> Unit,
    onResultShowed: () -> Unit,
    onSelectedUser: (User) -> Unit,
    onAmountChange: (String) -> Unit,
    onRetryError: () -> Unit,
    onCloseError: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
        if (result != null) {
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(result) {
                coroutineScope.launch { snackbarHostState.showSnackbar("Operations done correctly on ${result.users.toFloat() / users.count().toFloat() * 100}% of users!", duration = SnackbarDuration.Long) }.join()
                onResultShowed()
            }
        }
        if (error != null) {
            AlertDialogBulkOperation(
                message = error,
                onOk = onRetryError,
                onClose = onCloseError
            )
        }
        LoadedBulkOperationContent(
            amount = amount,
            isValidAmount = isValidAmount,
            users = users,
            onOperationExecute = onOperationExecute,
            onSelectedUser = onSelectedUser,
            onAmountChange = onAmountChange,
            modifier = modifier
        )
    }
}

@Composable
fun AlertDialogBulkOperation(
    message: String,
    onOk: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = @Composable {
            TextButton(onClick = onOk) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Close")
            }
        },
        icon = @Composable {
            Icon(Icons.Filled.Warning, contentDescription = "Error")
        },
        title = @Composable {
            Text("There was an error!")
        },
        text = @Composable {
            Text("Error Message: $message")
        })
}


@Composable
fun LoadedBulkOperationContent(
    amount: String,
    isValidAmount: Boolean,
    users: List<UserSelectable>,
    onOperationExecute: () -> Unit,
    onSelectedUser: (User) -> Unit,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        AmountTextBox(
            amount = amount,
            isValid = isValidAmount,
            onAmountChange = onAmountChange
        )
        UserSelectable(
            users = users,
            onUserClicked = onSelectedUser,
            modifier = Modifier.weight(0.7f)
        )
        Button(
            onClick = onOperationExecute
        ) {
            Text("Do operation")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BulkOperationScreenPreview() {
    BulkOperationScreen(
        error = null,
        result = null,
        isLoading = false,
        amount = "100",
        isValidAmount = true,
        users = listOf(
            UserSelectable(User(Id("1"), "Pablo Fraile", Amount(90)), true),
            UserSelectable(User(Id("2"), "John Doe", Amount(100)), true),
            UserSelectable(User(Id("3"), "Jane Doe", Amount(-100)), false),
        )
    )
}
