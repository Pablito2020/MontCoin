package com.pablofraile.montcoin.ui.transactions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Transaction
import com.pablofraile.montcoin.model.Transactions
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TransactionsViewModel : ViewModel() {

    private val _index = MutableStateFlow(10)

    @RequiresApi(Build.VERSION_CODES.O)
    private val _transactions: MutableStateFlow<Transactions> = MutableStateFlow(getFirstIteration())
    @RequiresApi(Build.VERSION_CODES.O)
    val transactions = _transactions

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshTransactions() {
        delay(2000)
        _transactions.value = getFirstIteration()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMoreOperations() {
        val begin = _index.value + 1
        val end = begin + 10
        val newTransacions = mutableListOf<Transaction>()
        for (i in begin..end)
            newTransacions.add(getMockOperation(i))
        val newResult = _transactions.value.plus(newTransacions)
        val scope = CoroutineScope(Dispatchers.IO)
        Log.e("Current", "Current is: ${newResult.map{it.user.name}}")
        _index.value = end + 1
        scope.launch {
            _transactions.emit(newResult)
        }
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

@RequiresApi(Build.VERSION_CODES.O)
fun getFirstIteration(): List<Transaction> {
    val transactions = mutableListOf<Transaction>()
    for (i in 0..10)
        transactions.add(getMockOperation(i))
    return transactions
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMockOperation(iteration: Int): Transaction {
    val random = (0..1000).random().toString()
    val user = User(Id("Id-$iteration"), "User $iteration")
    val amount = Amount(random)
    val date = Date.from(Instant.now())
    return Transaction(user, amount, date)
}
