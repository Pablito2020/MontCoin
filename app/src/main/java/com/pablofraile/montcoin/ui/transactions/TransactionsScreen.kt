package com.pablofraile.montcoin.ui.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pablofraile.montcoin.model.Transactions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    transactions: Transactions
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
        )
    }
}

@Composable
fun TransactionsContent(
    transactions: Transactions,
    modifier: Modifier = Modifier,
) {
    ShowTransactions(transactions.transactions.map { it.toString() }, modifier = modifier)
}

@Composable
fun ShowTransactions(
    transactions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        transactions.forEach { item ->
            ListItem(headlineContent = {
                ShowTransaction(
                    item
                )
            })
            Divider()
        }
    }

}

@Composable
fun ShowTransaction(it: String) {
    Text(text = it)
}

