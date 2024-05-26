package com.pablofraile.montcoin.model

data class Transaction(val user: User, val amount: Amount)

typealias Transactions = List<Transaction>