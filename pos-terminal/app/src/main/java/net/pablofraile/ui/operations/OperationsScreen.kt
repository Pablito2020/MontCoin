package net.pablofraile.ui.operations

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pablofraile.model.Amount
import net.pablofraile.model.Id
import net.pablofraile.model.Operation
import net.pablofraile.model.Operations
import net.pablofraile.model.User
import net.pablofraile.ui.common.InfiniteScroll
import net.pablofraile.ui.theme.LoseMoneyColor
import net.pablofraile.ui.theme.WinMoneyColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsScreen(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    operations: Operations,
    loadMoreItems: () -> Unit,
    onRefresh: suspend () -> Unit,
    onReload: suspend () -> Unit,
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
            if (errorMessage != null) {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = errorMessage
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterHorizontally)
                                .clickable {
                                    coroutineScope.launch {
                                        onRefresh()
                                    }
                                },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(
                                        ButtonDefaults.shape
                                    )
                                    .background(MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text("Try again", modifier = Modifier.padding(10.dp))
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = "reload",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Operations(
                    operations = operations,
                    onRefresh = onRefresh,
                    onReload = onReload,
                    loadMoreItems = loadMoreItems,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun Operations(
    operations: List<Operation>,
    onRefresh: suspend () -> Unit,
    onReload: suspend () -> Unit,
    loadMoreItems: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    if (operations.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No operations found!")
            Spacer(modifier = Modifier.height(10.dp))
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        coroutineScope.launch {
                            onReload()
                        }
                    },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(
                            ButtonDefaults.shape
                        )
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Refresh", modifier = Modifier.padding(10.dp))
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "reload",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    } else {
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
                Text(
                    text = "\uD83D\uDCB0 $amountValue Coins",
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
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
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(WinMoneyColor)
                )
            } else {
                Image(
                    imageVector = Icons.Default.MoneyOff,
                    contentDescription = "Lose Money",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(LoseMoneyColor)
                )
            }
        }
    }
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
//        errorMessage = "Error loading operations",
        operations = operations,
        onReload = {}
    )
}
