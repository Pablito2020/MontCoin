package com.pablofraile.montcoin.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Amount(val value: String) {

    override fun toString(): String {
        return value.toString()
    }

    fun isValid(): Boolean {
        try {
            value.toInt()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun isEmpty(): Boolean {
        return value.isEmpty()
    }

    fun toInt(): Int {
        if (!isValid())
            throw IllegalStateException("Can't convert amount if it has an invalid number")
        return value.toInt()
    }

}

data class TransactionUiState(
    val amount: Amount,
    val card: CreditCardState,
    val transaction: MontCoinTransactionState? = null
)


class TransactionViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            TransactionUiState(
                amount = Amount(value=""),
                card = CreditCardState.StoppedSearching
            )
        )
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun changeAmount(amount: String) {
        _uiState.update {
            it.copy(amount = Amount(value=amount))
        }
    }

    fun changeCardState(card: CreditCardState) {
        when (card) {
            CreditCardState.StoppedSearching, CreditCardState.SearchingCard -> {
                _uiState.update { it.copy(card = card) }
            }
            is CreditCardState.FoundCard -> {
                viewModelScope.launch {
                    handleFoundCard(user = card.userId)
                    _uiState.update { it.copy(card=CreditCardState.SearchingCard) }
                }
            }
        }
    }

    private suspend fun handleFoundCard(user: String) {
        val amount =  _uiState.value.amount
        if (amount.isValid() && !amount.isEmpty())
            writeTransaction(user = user, amount = amount.toInt())
        else
            markTransactionAsInvalid(message = "Invalid Amount!")
    }

    private fun markTransactionAsInvalid(message: String) {
        _uiState.update {
            it.copy(
                transaction = MontCoinTransactionState.TransactionError(
                    message = message
                )
            )
        }
    }

    private suspend fun writeTransaction(user: String, amount: Int) {
        _uiState.update { it.copy(transaction = MontCoinTransactionState.DoingTransaction) }
        val goodTransaction = true
        delay(2000)
        print("Doing transaction $amount on user $user")
        if (goodTransaction)
            _uiState.update { it.copy(transaction = MontCoinTransactionState.TransactionSuccess) }
        else
            markTransactionAsInvalid(message = "Transaction failed!")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransactionViewModel() as T
            }
        }
    }

}