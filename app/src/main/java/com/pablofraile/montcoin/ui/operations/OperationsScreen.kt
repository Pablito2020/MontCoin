package com.pablofraile.montcoin.ui.operations

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.R
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.InfiniteScroll
import com.pablofraile.montcoin.ui.theme.LoseMoneyColor
import com.pablofraile.montcoin.ui.theme.WinMoneyColor
import java.time.Instant
import java.util.Date
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsScreen(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    operations: Operations,
    loadMoreItems: () -> Unit,
    onRefresh: suspend () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Operations",
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
                        onClick = {
                            Toast.makeText(
                                context,
                                "Search is not yet implemented in this configuration",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Operations(
            operations = operations,
            onRefresh = onRefresh,
            loadMoreItems = loadMoreItems,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun Operations(
    operations: List<Operation>,
    onRefresh: suspend () -> Unit,
    loadMoreItems: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    InfiniteScroll(
        elements = operations,
        itemRender = { operation, modifier ->
            OperationItem(
                operation = operation,
                modifier = Modifier.padding(8.dp)
            )
        },
        onRefresh = onRefresh,
        loadMoreItems = loadMoreItems,
        refreshedMessage = "Loaded last Operations!",
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        cachingKey = { it.id }
    )
}

@Composable
fun OperationItem(operation: Operation, modifier: Modifier = Modifier) {
    val amountValue = operation.amount.value
    val isPositive = amountValue >= 0
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Column {
                Text(text = "\uD83D\uDCB0 $amountValue Coins", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "To: ${operation.user.name}"
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: ${operation.date}"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isPositive) {
                Image(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Win Money",
                    modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(WinMoneyColor)
                )
            } else {
                Image(
                    imageVector = Icons.Default.MoneyOff,
                    contentDescription = "Lose Money",
                    modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(LoseMoneyColor)
                )
            }
        }
    }
//        Row(
//            modifier = Modifier
//                .padding(start = 32.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
//                .animateContentSize(
//                    animationSpec = tween(
//                        durationMillis = 200,
//                        delayMillis = 0,
//                        easing = LinearEasing,
//                    ),
//                )
//                .clip(MaterialTheme.shapes.medium)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = "User: ${operation.user.name}",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = "Date: ${operation.date}",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//            Text(
//                text = operation.amount.toString(),
//                fontSize = 20.sp,
//                color = amountColor,
//                fontWeight = FontWeight.Bold
//            )
//        }
}

@Preview(showBackground = true)
@Composable
fun OperationsPreview() {
    val user = User(
        id = Id("1"),
        name = "Pablo Fraile",
        amount = Amount(100),
        numberOfOperations = 10
    )
    val operations = listOf(
        Operation(
            id = UUID.randomUUID(),
            amount = Amount(100),
            user = user,
            date = Date.from(Instant.now())
        ),
        Operation(
            id = UUID.randomUUID(),
            amount = Amount(-100),
            user = user,
            date = Date.from(Instant.now())
        )
    )
    OperationsScreen(
        openDrawer = {},
        snackbarHostState = SnackbarHostState(),
        loadMoreItems = {},
        onRefresh = {},
        operations = operations
    )
}
