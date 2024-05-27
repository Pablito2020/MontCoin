package com.pablofraile.montcoin.data.operations

import android.os.Build
import androidx.annotation.RequiresApi
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.model.WriteOperation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.util.Date

class InMemoryOperationRepository : OperationsRepository {

    val operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun execute(operation: WriteOperation): Result<Operation> {
        delay(2000)
        val newOperation = Operation(
            user = User(id = operation.userId, name = "FakeUser", amount = operation.amount),
            amount = operation.amount,
            date = Date.from(Instant.now())
        )
        operations.value += newOperation
        return Result.success(newOperation)
    }

    override fun observeOperations(): Flow<Operations> = operations

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchOperations(currentFetched: Int, toFetch: Int): Result<Unit> {
        delay(2000)
        return Result.success(Unit)
    }

}
