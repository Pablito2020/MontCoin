package net.pablofraile.data.operations

import android.util.Log
import net.pablofraile.data.api.OperationsApi
import net.pablofraile.model.Amount
import net.pablofraile.model.BulkOperation
import net.pablofraile.model.BulkOperationResult
import net.pablofraile.model.HourOperationsStats
import net.pablofraile.model.Id
import net.pablofraile.model.Operation
import net.pablofraile.model.User
import net.pablofraile.model.WriteOperation
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

class BackendOperationRepository(private val operationsApi: OperationsApi) : OperationsRepository {
    override suspend fun execute(operation: WriteOperation): Result<Operation> {
        return operationsApi.makeOperation(operation).map {
            Operation(
                id = UUID.fromString(it.id),
                amount = Amount(it.amount),
                user = User(
                    id = Id(it.user.id),
                    name = it.user.name,
                    amount = Amount(it.user.amount),
                    numberOfOperations = it.user.operations_with_card
                ),
                date = it.date.toDate()
            )
        }
    }

    override suspend fun execute(operations: BulkOperation): Result<BulkOperationResult> {
        return operationsApi.makeBulkOperation(operations).map {
            BulkOperationResult(users=it.num_users, amount=Amount(it.amount))
        }
    }

    override suspend fun getOperationsFor(userId: Id): Result<List<Operation>> {
        return operationsApi.getOperationsFor(userId.value).map {
            it.map { operationApi ->
                Operation(
                    id = UUID.fromString(operationApi.id),
                    amount = Amount(operationApi.amount),
                    user = User(
                        id = Id(operationApi.user.id),
                        name = operationApi.user.name,
                        amount = Amount(operationApi.user.amount),
                        numberOfOperations = operationApi.user.operations_with_card
                    ),
                    date = operationApi.date.toDate()
                )
            }
        }
    }

    override suspend fun getOperations(page: Int, size: Int): Result<List<Operation>> {
        return operationsApi.getOperations(page, size).map {
            it.items.map { operationApi ->
                Operation(
                    id = UUID.fromString(operationApi.id),
                    amount = Amount(operationApi.amount),
                    user = User(
                        id = Id(operationApi.user.id),
                        name = operationApi.user.name,
                        amount = Amount(operationApi.user.amount),
                        numberOfOperations = operationApi.user.operations_with_card
                    ),
                    date = operationApi.date.toDate()
                )
            }
        }
    }

    override suspend fun getOperationsForToday(): Result<List<HourOperationsStats>> {
        return operationsApi.getOperationsToday().map {
            it.map { operationToday ->
                HourOperationsStats(
                    positiveAmount = Amount(operationToday.positive_amount),
                    negativeAmount = Amount(operationToday.negative_amount),
                    hour = operationToday.hour,
                )
            }
        }
    }
}


fun Int.toDate(): Date {
    return Date(this.toEpochMillis())
}

fun Int.toEpochMillis(): Long {
    return TimeUnit.SECONDS.toMillis(this.toLong())
}