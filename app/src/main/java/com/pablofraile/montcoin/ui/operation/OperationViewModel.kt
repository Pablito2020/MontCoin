package com.pablofraile.montcoin.ui.operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OperationUiState(
    val amount: Amount,
    val card: CreditCardState,
    val operation: MontCoinOperationState? = null
)


class OperationViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            OperationUiState(
                amount = Amount(value=""),
                card = CreditCardState.StoppedSearching
            )
        )
    val uiState: StateFlow<OperationUiState> = _uiState.asStateFlow()

    fun changeAmount(amount: String) {
        _uiState.update {
            it.copy(amount = Amount(value=amount))
        }
    }

    fun changeCardState(card: CreditCardState) {
        when (card) {
            CreditCardState.StoppedSearching -> {
                _uiState.update { it.copy(card = card) }
            }
            CreditCardState.SearchingCard -> {
                if (!uiState.value.amount.isEmpty() && uiState.value.amount.isValid())
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
                operation = MontCoinOperationState.Error(
                    message = message
                )
            )
        }
    }

    private suspend fun writeTransaction(user: String, amount: Int) {
        _uiState.update { it.copy(operation = MontCoinOperationState.DoingIt) }
        val goodTransaction = true
        delay(2000)
        print("Doing transaction $amount on user $user")
        if (goodTransaction)
            _uiState.update { it.copy(operation = MontCoinOperationState.Success) }
        else
            markTransactionAsInvalid(message = "Transaction failed!")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OperationViewModel() as T
            }
        }
    }

}