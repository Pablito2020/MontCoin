package net.pablofraile.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import net.pablofraile.model.Amount
import net.pablofraile.model.Id
import net.pablofraile.model.Operation
import net.pablofraile.model.User
import net.pablofraile.ui.common.AnimatedCircle
import net.pablofraile.ui.operations.OperationItem
import net.pablofraile.ui.theme.LoseMoneyColor
import net.pablofraile.ui.theme.WinMoneyColor

@Composable
fun UserScreen(
    isLoading: Boolean,
    user: User?,
    operations: List<Operation>,
    errorMessage: String?,
    percentage: Percentage?,
    onOkError: () -> Unit = {},
    onRetryError: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    UserComponent(
        operations = operations,
        user = user,
        percentage = percentage,
        modifier = modifier,
        isLoading = isLoading
    )
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
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user != null && percentage != null) {
                    when (percentage) {
                        is Percentage.Empty -> {
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
//                            val circleColors = listOf(
//                                WinMoneyColor,
//                                LoseMoneyColor
//                            )
//                            Text(
//                                text = "Amount",
//                                style = MaterialTheme.typography.bodySmall,
//                                modifier = Modifier.align(Alignment.CenterHorizontally)
//                            )
//                            Text(
//                                text = "${user.amount} \uD83D\uDCB8",
//                                style = MaterialTheme.typography.headlineMedium,
//                                modifier = Modifier.align(Alignment.CenterHorizontally)
//                            )
//                            AnimatedCircle(
//                                percentage.toList(),
//                                circleColors,
//                                Modifier
//                                    .height(300.dp)
//                                    .align(Alignment.CenterHorizontally)
//                                    .fillMaxWidth()
//                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (operations.isEmpty()) {
                    EmptyOperations()
                } else {
                    ListOperations(operations = operations)
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
//
//
//@Composable
//fun UserScreen(
//    isLoading: Boolean,
//    user: User?,
//    operations: List<Operation>,
//    errorMessage: String?,
//    percentage: Percentage?,
//    onOkError: () -> Unit = {},
//    onRetryError: () -> Unit = {},
//    modifier: Modifier = Modifier,
//) {
//    UserComponent(
//        operations = operations,
//        user = user,
//        percentage = percentage,
//        modifier = modifier,
//        isLoading = isLoading
//    )
//}
//
//@Composable
//fun UserComponent(
//    operations: List<Operation>,
//    isLoading: Boolean,
//    user: User?,
//    percentage: Percentage?,
//    modifier: Modifier = Modifier,
//) {
//    if (isLoading) {
//        LoadingCircular()
//    } else {
//        Column(
//            modifier = modifier
//                .fillMaxSize()
//        ) {
//            if (user != null && percentage != null) {
//                when (percentage) {
//                    is Percentage.Empty -> {
//                        Box(
//                            modifier = Modifier
//                                .padding(16.dp)
//                                .align(Alignment.CenterHorizontally)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .align(Alignment.Center)
//                                    .fillMaxSize(),
//                                verticalArrangement = Arrangement.SpaceEvenly,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Column {
//                                    Text(
//                                        text = "Amount",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        modifier = Modifier.align(Alignment.CenterHorizontally)
//                                    )
//                                    Text(
//                                        text = "${user.amount} \uD83D\uDCB8",
//                                        style = MaterialTheme.typography.headlineMedium,
//                                        modifier = Modifier.align(Alignment.CenterHorizontally)
//                                    )
//                                }
//                                HorizontalDivider()
//                                Text(
//                                    text = "No operations found \uD83E\uDEF0",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
//                                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                                )
//                            }
//                        }
//                    }
//
//                    is PercentageV -> {
//                        Box(Modifier.padding(16.dp)) {
//                            val circleColors = listOf(
//                                WinMoneyColor,
//                                LoseMoneyColor
//                            )
//                            AnimatedCircle(
//                                percentage.toList(),
//                                circleColors,
//                                Modifier
//                                    .height(300.dp)
//                                    .align(Alignment.Center)
//                                    .fillMaxWidth()
//                            )
//                            Column(modifier = Modifier.align(Alignment.Center)) {
//                                Text(
//                                    text = "Amount",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                                )
//                                Text(
//                                    text = "${user.amount} \uD83D\uDCB8",
//                                    style = MaterialTheme.typography.headlineMedium,
//                                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                                )
//                            }
//                        }
//                        HorizontalDivider()
//                        Operations(
//                            operations = operations
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun LoadingCircular() {
//    Box(
//        modifier = Modifier
//            .padding(12.dp)
//            .fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator(
//            color = MaterialTheme.colorScheme.secondary,
//            trackColor = MaterialTheme.colorScheme.surfaceVariant,
//            modifier = Modifier.align(Alignment.Center)
//        )
//    }
//}
//
//@Composable
//fun ColumnScope.Operations(
//    operations: List<Operation>,
//) {
//    if (operations.isEmpty())
//        EmptyOperations()
//    else
//        ListOperations(operations = operations)
//}
//
//@Composable
//fun ColumnScope.EmptyOperations() {
//    Text(
//        text = "No operations found \uD83E\uDEF0",
//        style = MaterialTheme.typography.bodyLarge,
//        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
//        modifier = Modifier
//            .padding(20.dp)
//            .align(Alignment.CenterHorizontally)
//    )
//
//}
//
@Composable
fun ListOperations(
    operations: List<Operation>,
) {
    LazyColumn(modifier = Modifier.padding(12.dp)) {
        this.items(operations.size, key = { it -> operations[it].id.toString() }) { index ->
            OperationItem(
                operation = operations[index],
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
//
@Preview(showBackground = true)
@PreviewScreenSizes
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
