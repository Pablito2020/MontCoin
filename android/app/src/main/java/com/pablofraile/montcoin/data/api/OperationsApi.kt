package com.pablofraile.montcoin.data.api

import android.util.Log

data class OperationToday(val positive_amount: Int, val negative_amount: Int, val hour: Int)

open class WriteOperation(
    val amount: Int,
    val should_fail_if_not_enough_money: Boolean,
    val with_credit_card: Boolean
)

object OperationsApi {

    suspend fun getOperationsToday(): Result<List<OperationToday>> {
        val x = createOperation()
        Log.e("OperationsApi", "getOperationsToday: $x")
        return CommonApi.get("/operations/today")
    }

    suspend fun createOperation(): Result<Any> {
        val userId = "7bac45c3-7201-4ff5-a942-00deb0ebf476"
        val signedMessage: SignedWriteOperation = WriteOperation(
                100,
                false,
                false
            ).sign()
        return CommonApi.post("/operation/user/$userId", signedMessage)
    }

}