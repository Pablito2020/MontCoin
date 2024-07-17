package net.pablofraile.data.operations

import net.pablofraile.model.Id
import net.pablofraile.model.Operation
import net.pablofraile.model.WriteOperation
import net.pablofraile.model.BulkOperation
import net.pablofraile.model.BulkOperationResult
import net.pablofraile.model.HourOperationsStats

interface OperationsRepository {
    suspend fun execute(operation: WriteOperation): Result<Operation>
    suspend fun execute(operations: BulkOperation): Result<BulkOperationResult>
    suspend fun getOperationsFor(userId: Id): Result<List<Operation>>
    suspend fun getOperations(page: Int, size: Int = 20): Result<List<Operation>>
    suspend fun getOperationsForToday(): Result<List<HourOperationsStats>>
}
