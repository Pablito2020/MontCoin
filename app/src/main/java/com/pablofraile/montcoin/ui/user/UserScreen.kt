package com.pablofraile.montcoin.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.VerticalDivider
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
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    isLoading: Boolean,
    user: User?,
    operations: List<Operation>,
    errorMessage: String?,
    percentage: Percentage?,
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
            percentage = percentage,
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
    percentage: Percentage?,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        LoadingCircular()
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (user != null && percentage != null) {
                when (percentage) {
                    is Percentage.Empty -> {
                        Box(modifier= Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                        ) {
                            Column(modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column {
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
                                HorizontalDivider()
                                Text(
                                    text = "No operations found \uD83E\uDEF0",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                    is PercentageV -> {
                        Box(Modifier.padding(16.dp)) {
                            val circleColors = listOf(
                                WinMoneyColor,
                                LoseMoneyColor
                            )
                            AnimatedCircle(
                                percentage.toList(),
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
                        HorizontalDivider()
                        Operations(
                            operations = operations
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingCircular() {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
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
        isLoading = false,
        operations = emptyList(),
        errorMessage = null,
        percentage = Percentage.Empty
    )
}
