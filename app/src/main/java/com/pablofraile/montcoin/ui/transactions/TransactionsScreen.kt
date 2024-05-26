package com.pablofraile.montcoin.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun TransactionsScreen() {
    val transactions = listOf("Transaction 1", "Transaction 2", "Transaction 3")
    ShowTransactions(transactions)
}

@Composable
fun ShowTransactions(transactions: List<String>) {
    Column {
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

