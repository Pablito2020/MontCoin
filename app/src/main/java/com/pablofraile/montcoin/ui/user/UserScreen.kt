package com.pablofraile.montcoin.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.AnimatedCircle
import com.pablofraile.montcoin.ui.operations.OperationItem
import com.pablofraile.montcoin.ui.theme.LoseMoneyColor
import com.pablofraile.montcoin.ui.theme.WinMoneyColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    isLoading: Boolean,
    user: User?,
    operations: List<Operation>,
    errorMessage: String?,
    onOkError: () -> Unit = {},
    onRetryError: () -> Unit = {},
    onRefresh: () -> Unit = {},
    goBack: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = user?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(64.dp)
                        ) {
                            val state = PullToRefreshState(
                                positionalThresholdPx = 0.toFloat(),
                                initialRefreshing = true,
                                enabled = { true }
                            )
                            PullToRefreshDefaults.Indicator(
                                state = state,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onRefresh
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "refresh"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        UserComponent(
            operations = operations,
            user = user,
            modifier = modifier,
            isLoading = isLoading
        )
        if (errorMessage != null)
            AlertDialog(
                onDismissRequest = {},
                confirmButton = @Composable {
                    TextButton(onClick = onOkError) {
                        Text("Go Back")
                    }
                },
                dismissButton = @Composable {
                    TextButton(onClick = onRetryError) {
                        Text("Retry")
                    }
                },
                icon = @Composable {
                    Icon(Icons.Filled.Warning, contentDescription = "Error")
                },
                title = @Composable {
                    Text("Error Loading the Page!")
                },
                text = @Composable {
                    Text("The error was: $errorMessage.\n Do you want to retry?")
                })
    }
}

@Composable
fun UserComponent(
    operations: List<Operation>,
    isLoading: Boolean,
    user: User?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        user?.let {
            val totalSum =
                operations.sumOf { op -> if (op.amount.value < 0) op.amount.value * -1 else op.amount.value }
            if (totalSum == 0) {
                Amount(it, listOf(1f, 0f))
            } else {
                val negative =
                    operations.sumOf { op -> if (op.amount.value < 0) -1 * op.amount.value else 0 }
                val positive =
                    operations.sumOf { op -> if (op.amount.value > 0) op.amount.value else 0 }
                val percentages = listOf(
                    positive.toFloat() / totalSum,
                    negative.toFloat() / totalSum,
                )
                Amount(it, percentages)
            }
            HorizontalDivider()
        }
        if (isLoading) {
            LoadingCircular()
        } else {
            Operations(
                operations = operations
            )
        }
    }
}

@Composable
fun Amount(user: User, percentages: List<Float>) {
    Box(Modifier.padding(16.dp)) {
        val circleColors = listOf(
            WinMoneyColor,
            LoseMoneyColor
        )
        AnimatedCircle(
            percentages,
            circleColors,
            Modifier
                .height(300.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        )
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Amount",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "${user.amount} \uD83D\uDCB8",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ColumnScope.LoadingCircular() {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxHeight()
            .align(Alignment.CenterHorizontally),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ColumnScope.Operations(
    operations: List<Operation>,
) {
    if (operations.isEmpty())
        EmptyOperations()
    else
        ListOperations(operations = operations)
}

@Composable
fun ColumnScope.EmptyOperations() {
    Text(
        text = "No operations found \uD83E\uDEF0",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
        modifier = Modifier
            .padding(20.dp)
            .align(Alignment.CenterHorizontally)
    )

}

@Composable
fun ListOperations(
    operations: List<Operation>,
) {
    Column(modifier = Modifier.padding(12.dp)) {
        operations.forEach { item ->
            OperationItem(
                operation = item,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    val user = User(
        id = Id("1"),
        name = "Pablo Fraile",
        amount = Amount(1000),
        numberOfOperations = 10
    )
    UserScreen(
        user = User(
            id = Id("1"),
            name = "Pablo Fraile",
            amount = Amount(1000),
            numberOfOperations = 10
        ),
//        user=null,
        isLoading = true,
        operations = emptyList(),
        errorMessage = null
    )
}
