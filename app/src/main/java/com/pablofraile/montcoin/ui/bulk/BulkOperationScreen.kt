package com.pablofraile.montcoin.ui.bulk

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Unpublished
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.UserSelectable
import com.pablofraile.montcoin.ui.operation.AmountTextBox


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkOperationScreen(
    amount: String,
    isValidAmount: Boolean,
    users: List<UserSelectable>,
    onOperationExecute: () -> Unit = {},
    onSelectedUser: (User) -> Unit = {},
    toggleAllUsers: () -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    openDrawer: () -> Unit = {},
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
                    if (users.isNotEmpty()) {
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
            amount = amount,
            isValidAmount = isValidAmount,
            users = users,
            onOperationExecute = onOperationExecute,
            onSelectedUser = onSelectedUser,
            onAmountChange = onAmountChange,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

@Composable
fun BulkOperationContent(
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
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
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
        amount="100",
        isValidAmount = true,
        users = listOf(
            UserSelectable(User(Id("1"), "Pablo Fraile", Amount(90)), true),
            UserSelectable(User(Id("2"), "John Doe", Amount(100)), true),
            UserSelectable(User(Id("3"), "Jane Doe", Amount(-100)), false),
        )
    )
}
