package com.pablofraile.montcoin.model

data class Transaction(val user: User, val amount: Amount)

data class Transactions(val transactions: List<Transaction>)