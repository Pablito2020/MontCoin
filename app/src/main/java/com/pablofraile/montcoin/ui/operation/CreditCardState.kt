package com.pablofraile.montcoin.ui.operation

sealed class CreditCardState {
    object StoppedSearching : CreditCardState()
    object SearchingCard : CreditCardState()
    class FoundCard(val userId: String) : CreditCardState()
}

sealed class MontCoinOperationState {
    object Success : MontCoinOperationState()
    object DoingIt : MontCoinOperationState()
    class Error(val message: String) : MontCoinOperationState()
}