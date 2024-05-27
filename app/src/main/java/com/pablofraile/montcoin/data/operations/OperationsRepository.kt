package com.pablofraile.montcoin.data.operations

import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import kotlinx.coroutines.flow.Flow
import com.pablofraile.montcoin.model.Operations

interface OperationsRepository {
    suspend fun execute(operation: WriteOperation): Result<Operation>
    fun observeOperations(): Flow<Operations>
    suspend fun fetchOperations(currentFetched: Int = 0, toFetch: Int = 10): Result<Unit>
}
