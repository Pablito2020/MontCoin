package com.pablofraile.montcoin.ui.operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.nfc.NfcValues
import com.pablofraile.montcoin.nfc.ReadTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class OperationViewModel(nfc: Flow<ReadTag?>) : ViewModel() {

    private val _amount = MutableStateFlow(Amount(value = ""))
    val amount: StateFlow<Amount> = _amount
    fun changeAmount(amount: String) = _amount.update {
        it.copy(value = amount)
    }

    private val _cardState: MutableStateFlow<CreditCardState> =
        MutableStateFlow(CreditCardState.StoppedSearching)
    val cardState: StateFlow<CreditCardState> = _cardState
    fun changeCardState(card: CreditCardState) = _cardState.update { card }

    private val historyNfcValues: MutableStateFlow<NfcValues> = MutableStateFlow(NfcValues())
    private val nfcValues = nfc.combine(historyNfcValues) { new, current ->
        NfcValues(currentTag = new, lastTag = current.currentTag)
    }

    val operationStatus = combine(amount, cardState, nfcValues) { amount, card, nfc ->
        if (amount.isValid() && card == CreditCardState.SearchingCard && nfc.isDifferentTag ())
            return@combine writeTransaction("user", amount.toInt())
        return@combine null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)


    private val _isDoingOperation = MutableStateFlow(false)
    val isDoingOperation: StateFlow<Boolean> = _isDoingOperation


    private suspend fun writeTransaction(user: String, amount: Int): MontCoinOperationState {
        _isDoingOperation.emit(true)
        val goodTransaction = true
        delay(2000)
        _isDoingOperation.emit(false)
        if (goodTransaction)
            return MontCoinOperationState.Success
        return MontCoinOperationState.Error("Error writing transaction")
    }

    companion object {
        fun provideFactory(nfc: Flow<ReadTag?>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationViewModel(nfc) as T
                }
            }
    }

}