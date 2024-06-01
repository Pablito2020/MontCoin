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
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    isLoadingUser: Boolean,
    isLoadingOperations: Boolean,
    user: User?,
    operations: List<Operation>,
    onRefresh: () -> Unit = {},
    goBack: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    val context = LocalContext.current
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
                    if (isLoadingOperations) {
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
        if (isLoadingUser) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            UserComponent(
                isLoadingOperations = isLoadingOperations,
                operations = operations,
                user = user,
                modifier = modifier
            )
        }
    }
}

@Composable
fun UserComponent(
    isLoadingOperations: Boolean,
    operations: List<Operation>,
    user: User?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        user?.let { Amount(it) }
        HorizontalDivider()
        Operations(
            isLoading = isLoadingOperations,
            operations = operations
        )
    }
}

@Composable
fun Amount(user: User) {
    Box(Modifier.padding(16.dp)) {
        val accountsProportion = listOf(0.40f, 0.60f)
        val circleColors = listOf(
            WinMoneyColor,
            LoseMoneyColor
        )
        AnimatedCircle(
            accountsProportion,
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
fun ColumnScope.Operations(
    isLoading: Boolean,
    operations: List<Operation>,
) {
    if (isLoading)
        LoadingCircular()
    else
        Operations(operations = operations)
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
        isLoadingUser = false,
        isLoadingOperations = false,
        operations = emptyList(),
//        operations = (0..10).map {
//            Operation(
//                id = UUID.randomUUID(),
//                amount = Amount(it * 100),
//                date = Date.from(Instant.now()),
//                user = user
//            )
//        }
    )
}
