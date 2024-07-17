package net.pablofraile.data.operations

import android.os.Build
import androidx.annotation.RequiresApi
import net.pablofraile.data.api.OperationsApi
import net.pablofraile.data.users.UsersRepository
import net.pablofraile.model.Amount
import net.pablofraile.model.BulkOperation
import net.pablofraile.model.BulkOperationResult
import net.pablofraile.model.HourOperationsStats
import net.pablofraile.model.Id
import net.pablofraile.model.Operation
import net.pablofraile.model.Operations
import net.pablofraile.model.WriteOperation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.Date
import java.util.UUID

class InMemoryOperationRepository(
    val userRepository: UsersRepository
) : OperationsRepository {

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
        return Result.success(
            BulkOperationResult(
                operations.users.size,
                operations.amount,
            )
        )
    }

    override suspend fun getOperationsFor(userId: Id): Result<List<Operation>> {
        delay(2000)
        return Result.success(operations.value.filter { it.user.id == userId })
    }

    override suspend fun getOperations(page: Int, size: Int): Result<List<Operation>> {
        delay(2000)
        return Result.success(operations.value.drop(page * size).take(size))
    }

    override suspend fun getOperationsForToday(): Result<List<HourOperationsStats>> {
        val now = Date()
        val less24Hours = now.time - 24 * 60 * 60 * 1000
        val operationsFromNow =
            operations.value.filter { it.date.time >= less24Hours && it.date.time <= now.time }
        val stats = operationsFromNow.groupBy { (it.date.time / 60).toInt() }.map { (hour, operations) ->
            HourOperationsStats(
                positiveAmount = (operations.filter { operation -> operation.amount.value > 0 }
                    .map { operation -> operation.amount.value }
                    .reduceOrNull { acc, amount -> acc + amount }?.let { Amount(it) } ?: Amount(0)),
                negativeAmount = operations.filter { operation -> operation.amount.value < 0 }
                    .map { operation -> -(operation.amount.value) }
                    .reduceOrNull { acc, amount -> acc + amount }?.let { Amount(it) } ?: Amount(0),
                hour = ((now.time - operations.first().date.time) / (60 * 1000)).toInt()
            )
        }
        return Result.success(stats)
    }

}
