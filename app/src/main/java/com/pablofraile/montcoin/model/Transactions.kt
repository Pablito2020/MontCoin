package com.pablofraile.montcoin.model

import java.util.Date

data class Transaction(val user: User, val amount: Amount, val date: Date)

typealias Transactions = List<Transaction>