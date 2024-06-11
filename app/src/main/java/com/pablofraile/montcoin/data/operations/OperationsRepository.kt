package com.pablofraile.montcoin.data.operations

import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.BulkOperationResult

interface OperationsRepository {
    suspend fun execute(operation: WriteOperation): Result<Operation>
    suspend fun execute(operations: BulkOperation): Result<BulkOperationResult>
    suspend fun getOperationsFor(userId: Id): Result<List<Operation>>
    suspend fun getOperations(page: Int, size: Int = 20): Result<List<Operation>>
}
