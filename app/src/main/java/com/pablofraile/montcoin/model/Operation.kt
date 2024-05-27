package com.pablofraile.montcoin.model

import java.util.Date

data class Operation(val user: User, val amount: Amount, val date: Date)

typealias Operations = List<Operation>

data class Amount(val value: String) {

    fun isValid(): Boolean {
        return value.toIntOrNull() != null && value.isNotEmpty()
    }

    fun toInt(): Int {
        require(isValid()) { "Can't convert amount if it has an invalid number" }
        return value.toInt()
    }
}

data class WriteOperation(val userId: Id, val amount: Amount)