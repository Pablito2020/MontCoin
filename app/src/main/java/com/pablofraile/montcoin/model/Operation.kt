package com.pablofraile.montcoin.model

data class Operation(val userId: Id, val amount: Amount)

data class Amount(val value: String) {

    fun isValid(): Boolean {
        return value.toIntOrNull() != null && value.isNotEmpty()
    }

    fun toInt(): Int {
        require(isValid()) { "Can't convert amount if it has an invalid number" }
        return value.toInt()
    }
}

sealed class Result {
    data object Success : Result()
    class Error(val message: String) : Result()
}
