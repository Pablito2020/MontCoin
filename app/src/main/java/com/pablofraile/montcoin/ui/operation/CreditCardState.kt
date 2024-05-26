package com.pablofraile.montcoin.ui.operation

sealed class CreditCardState {
    data object StoppedSearching : CreditCardState()
    data object SearchingCard : CreditCardState()
}

sealed class MontCoinOperationState {
    data object Success : MontCoinOperationState()
    class Error(val message: String) : MontCoinOperationState()
}