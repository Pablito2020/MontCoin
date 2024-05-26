package com.pablofraile.montcoin.ui.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pablofraile.montcoin.model.Transaction
import com.pablofraile.montcoin.model.Transactions
import com.pablofraile.montcoin.ui.operation.ShowSnackBar
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    transactions: Transactions,
    onRefresh: suspend () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transactions",
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
        TransactionsContent(
            transactions = transactions,
            modifier = Modifier.padding(innerPadding),
            onRefresh = onRefresh,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsContent(
    transactions: Transactions,
    modifier: Modifier = Modifier,
    onRefresh: suspend () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberPullToRefreshState()
    if (state.isRefreshing){
        LaunchedEffect(true) {
            onRefresh()
            val showSnackBar = coroutineScope.launch {snackbarHostState.showSnackbar("Refreshed Transactions!")}
            state.endRefresh()
            showSnackBar.join()
        }
    }
    Box(modifier= modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyColumn(modifier = modifier) {
            items(transactions) {
                TransactionItem(transaction = it, modifier = modifier)
            }
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state,
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction, modifier: Modifier) {
    val amountValue = transaction.amount.toInt()
    val amountColor = if (amountValue >= 0) Color.Green else Color.Red
    Card {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // User Name
                Text(
                    text = "User: ${transaction.user.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // User Id
                Text(
                    text = "Id: ${transaction.user.id.value}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = transaction.amount.value,
                fontSize = 20.sp,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
