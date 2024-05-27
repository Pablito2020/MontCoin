package com.pablofraile.montcoin.model

data class Id(val value: String) {
    fun toByteArray(): ByteArray = value.toByteArray()
}

data class User(val id: Id, val name: String, val amount: Amount)