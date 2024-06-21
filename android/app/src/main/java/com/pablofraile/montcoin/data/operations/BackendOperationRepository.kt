package com.pablofraile.montcoin.data.operations

import android.util.Log
import com.pablofraile.montcoin.data.api.OperationsApi
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.BulkOperationResult
import com.pablofraile.montcoin.model.HourOperationsStats
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation

class BackendOperationRepository : OperationsRepository {
    override suspend fun execute(operation: WriteOperation): Result<Operation> {
        return Result.failure(Exception("Not implemented"))
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