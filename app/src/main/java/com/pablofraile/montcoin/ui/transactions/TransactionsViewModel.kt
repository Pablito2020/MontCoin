package com.pablofraile.montcoin.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Transaction
import com.pablofraile.montcoin.model.Transactions
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.MutableStateFlow

class TransactionsViewModel : ViewModel() {

    private val _transactions = MutableStateFlow(
        Transactions(
            listOf(
                Transaction(user = User(Id("1"), "User 1"), amount = Amount("100")),
                Transaction(user = User(Id("2"), "User 2"), amount = Amount("300")),
                Transaction(user = User(Id("3"), "User 3"), amount = Amount("-1000")),
            )
        )
    )
    val transactions = _transactions
    fun refreshTransactions() {
        _transactions.value = Transactions(
            listOf(
                Transaction(user = User(Id("1"), "User 1"), amount = Amount("100")),
                Transaction(user = User(Id("2"), "User 2"), amount = Amount("300")),
                Transaction(user = User(Id("3"), "User 3"), amount = Amount("-1000")),
                Transaction(user = User(Id("4"), "User 4"), amount = Amount("500")),
            )
        )
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TransactionsViewModel() as T
                }
            }
    }


}