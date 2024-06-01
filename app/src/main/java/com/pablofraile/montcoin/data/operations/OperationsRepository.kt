package com.pablofraile.montcoin.data.operations

import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import kotlinx.coroutines.flow.Flow
import com.pablofraile.montcoin.model.Operations

interface OperationsRepository {
    suspend fun execute(operation: WriteOperation): Result<Operation>
    suspend fun getOperationsFor(userId: Id): Result<List<Operation>>
    suspend fun getOperations(page: Int, size: Int = 20): Result<List<Operation>>
}
