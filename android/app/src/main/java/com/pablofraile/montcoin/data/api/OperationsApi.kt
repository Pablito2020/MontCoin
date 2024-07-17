package com.pablofraile.montcoin.data.api

import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.WriteOperation

data class OperationToday(val positive_amount: Int, val negative_amount: Int, val hour: Int)

open class WriteOperationApi(
    val amount: Int,
    val should_fail_if_not_enough_money: Boolean,
    val with_credit_card: Boolean
)

open class BulkOperationApi(
    val users: List<String>,
    val amount: Int
)

class SignedBulkOperation(
    users: List<String>,
    amount: Int,
    override val signature: String
) : BulkOperationApi(users, amount), Signed

class SignedWriteOperationApi(
    amount: Int,
    should_fail_if_not_enough_money: Boolean,
    with_credit_card: Boolean,
    override val signature: String,
) : WriteOperationApi(amount, should_fail_if_not_enough_money, with_credit_card), Signed

class OperationApi(
    val id: String,
    val amount: Int,
    val user: UserApi,
    val date: Int
)

class BulkOperationResult(
    val amount: Int,
    val num_users: Int
)

class PaginatedOperations(
    val page: Int,
    val size: Int,
    val total: Int,
    val pages: Int,
    val items: List<OperationApi>
)

class OperationsApi(apiUrl: String, credentials: Credentials) : CommonApi(apiUrl, credentials) {

    suspend fun getOperationsToday(): Result<List<OperationToday>> {
        return get("/operations/today")
    }

    suspend fun makeOperation(writeOperation: WriteOperation): Result<OperationApi> {
        val userId = writeOperation.userId.value
        val signedMessage: SignedWriteOperationApi = WriteOperationApi(
            writeOperation.amount.value,
            writeOperation.shouldFailIfNotEnough,
            writeOperation.withCard
        ).sign(credentials)
        return post("/operation/user/$userId", signedMessage)
    }

    suspend fun makeBulkOperation(operation: BulkOperation): Result<BulkOperationResult> {
        val signedOperation: SignedBulkOperation = BulkOperationApi(
            users=operation.users.map { it.value },
            amount=operation.amount.value
        ).sign(credentials)
        return post("/operations/bulk", signedOperation)
    }

    suspend fun getOperationsFor(userId: String): Result<List<OperationApi>> {
        return get("/operations/user/$userId")
    }

    suspend fun getOperations(page: Int, size: Int): Result<PaginatedOperations> {
        return get("/operations?page=$page&size=$size")
    }

}