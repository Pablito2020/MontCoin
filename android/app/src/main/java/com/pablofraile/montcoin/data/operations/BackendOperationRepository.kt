package com.pablofraile.montcoin.data.operations

import android.util.Log
import com.pablofraile.montcoin.data.api.OperationsApi
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.BulkOperationResult
import com.pablofraile.montcoin.model.HourOperationsStats
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.model.WriteOperation
import java.util.Date
import java.util.UUID

class BackendOperationRepository : OperationsRepository {
    override suspend fun execute(operation: WriteOperation): Result<Operation> {
        return OperationsApi.makeOperation(operation).map {
            Operation(
                id = UUID.fromString(it.id),
                amount = Amount(it.amount),
                user = User(
                    id = Id(it.user.id),
                    name = it.user.name,
                    amount = Amount(it.user.amount),
                    numberOfOperations = it.user.operations_with_card
                ),
                date = Date(it.date)
            )
        }
    }

    override suspend fun execute(operations: BulkOperation): Result<BulkOperationResult> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun getOperationsFor(userId: Id): Result<List<Operation>> {
        return Result.success(emptyList())
    }

    override suspend fun getOperations(page: Int, size: Int): Result<List<Operation>> {
        Log.e("BackendOperationRepository", "getOperations not implemented")
        return Result.success(emptyList())
    }

    override suspend fun getOperationsForToday(): Result<List<HourOperationsStats>> {
        return OperationsApi.getOperationsToday().map {
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