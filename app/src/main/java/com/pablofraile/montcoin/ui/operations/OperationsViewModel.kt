package com.pablofraile.montcoin.ui.operations

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class OperationsViewModel : ViewModel() {

    private val _index = MutableStateFlow(10)

    @RequiresApi(Build.VERSION_CODES.O)
    private val _operations: MutableStateFlow<Operations> = MutableStateFlow(getFirstIteration())
    @RequiresApi(Build.VERSION_CODES.O)
    val operations = _operations

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshOperations() {
        delay(2000)
        _operations.value = getFirstIteration()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMoreOperations() {
        val begin = _index.value + 1
        val end = begin + 10
        val newTransacions = mutableListOf<Operation>()
        for (i in begin..end)
            newTransacions.add(getMockOperation(i))
        val newResult = _operations.value.plus(newTransacions)
        val scope = CoroutineScope(Dispatchers.IO)
        Log.e("Current", "Current is: ${newResult.map{it.user.name}}")
        _index.value = end + 1
        scope.launch {
            _operations.emit(newResult)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationsViewModel() as T
                }
            }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun getFirstIteration(): List<Operation> {
    val operations = mutableListOf<Operation>()
    for (i in 0..10)
        operations.add(getMockOperation(i))
    return operations
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMockOperation(iteration: Int): Operation {
    val random = (0..1000).random().toString()
    val user = User(Id("Id-$iteration"), "User $iteration")
    val amount = Amount(random)
    val date = Date.from(Instant.now())
    return Operation(user, amount, date)
}
