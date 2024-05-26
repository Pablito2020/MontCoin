package com.pablofraile.montcoin.data.operations

import android.os.Build
import androidx.annotation.RequiresApi
import com.pablofraile.montcoin.data.Result
import com.pablofraile.montcoin.data.Result.Success
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.model.WriteOperation
import com.pablofraile.montcoin.model.WriteOperationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.util.Date

class InMemoryOperationRepository : OperationsRepository {

    val operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun execute(operation: WriteOperation): Result<WriteOperationResult> {
        delay(2000)
        val newOperation = Operation(
            user = User(id = operation.userId, name = "FakeUser", amount = operation.amount),
            amount = operation.amount,
            date = Date.from(Instant.now())
        )
        operations.value += newOperation
        return Success(WriteOperationResult.Success)
    }

    override fun observeOperations(): Flow<Operations> = operations

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchOperations(currentFetched: Int, toFetch: Int): Result<Unit> {
        delay(2000)
//        if (currentFetched < 0) return Result.Error(IllegalArgumentException("currentFetched must be >= 0"))
//        if (toFetch <= 0) return  Result.Error(IllegalArgumentException("toFetch must be > 0"))
//        if (currentFetched + toFetch <= operations.value.size) return Success(Unit)
//        if (currentFetched < operations.value.size)
//            operations.value = operations.value.subList(0, currentFetched)
//        val newOperations = (currentFetched until currentFetched + toFetch).map { getMockOperation(it) }
//        operations.value += newOperations
        return Success(Unit)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getMockOperation(iteration: Int): Operation {
    val random = (0..100).random()
    val user = User(Id("Id-$iteration"), "User $iteration", Amount(random.toString()))
    val amount = Amount(iteration.toString())
    val date = Date(2323223232L)
    return Operation(user, amount, date)
}
