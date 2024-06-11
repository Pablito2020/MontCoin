package com.pablofraile.montcoin.data.operations

import android.os.Build
import androidx.annotation.RequiresApi
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.BulkOperationResult
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.WriteOperation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.Date
import java.util.UUID

class InMemoryOperationRepository(
    val userRepository: UsersRepository
): OperationsRepository {

    val operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun execute(operation: WriteOperation): Result<Operation> {
        delay(2000)
        val user = userRepository.getUserById(operation.userId.value)
        if (user.isFailure) return Result.failure(user.exceptionOrNull()!!)
        val newOperation = Operation(
            id = UUID.randomUUID(),
            user = user.getOrNull()!!,
            amount = operation.amount,
            date = Date.from(Instant.now())
        )
        operations.update { listOf(newOperation) + it }
        return Result.success(newOperation)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun execute(operations: BulkOperation): Result<BulkOperationResult> {
        delay(2000)
        return Result.success(BulkOperationResult(operations.users, operations.amount, Date.from(Instant.now())))
    }

    override suspend fun getOperationsFor(userId: Id): Result<List<Operation>> {
        delay(2000)
        return Result.success(operations.value.filter { it.user.id == userId })
    }

    override suspend fun getOperations(page: Int, size: Int): Result<List<Operation>> {
        delay(2000)
        return Result.success(operations.value.drop(page * size).take(size))
    }

}
