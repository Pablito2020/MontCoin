package com.pablofraile.montcoin.ui.operation

sealed class CreditCardState {
    object StoppedSearching : CreditCardState()
    object SearchingCard : CreditCardState()
    class FoundCard(val userId: String) : CreditCardState()
}

sealed class MontCoinTransactionState {
    object TransactionSuccess : MontCoinTransactionState()
    object DoingTransaction : MontCoinTransactionState()
    class TransactionError(val message: String) : MontCoinTransactionState()
}