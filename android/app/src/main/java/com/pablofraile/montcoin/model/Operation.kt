package com.pablofraile.montcoin.model

import java.util.Date
import java.util.UUID

data class HourOperationsStats(
    val positiveAmount: Amount,
    val negativeAmount: Amount,
    val hour: Int
)

data class Operation(val id: UUID, val user: User, val amount: Amount, val date: Date)

typealias Operations = List<Operation>

data class Amount(val value: Int) {
    override fun toString(): String = value.toString()
}

internal fun String.isValidAmount(): Boolean = this.toIntOrNull() != null && this.isNotEmpty()
internal fun String.toAmount(): Result<Amount> {
    if (!isValidAmount()) return Result.failure(IllegalArgumentException("Invalid Amount"))
    return Result.success(Amount(this.toInt()))
}

data class WriteOperation(val userId: Id, val amount: Amount, val withCard: Boolean = false)

data class BulkOperation(val users: List<Id>, val amount: Amount)
data class BulkOperationResult(val users: Int, val amount: Amount)
