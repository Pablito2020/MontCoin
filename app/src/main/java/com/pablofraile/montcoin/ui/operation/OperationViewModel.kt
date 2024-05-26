package com.pablofraile.montcoin.ui.operation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.nfc.ReadTag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class OperationViewModel(nfc: Flow<ReadTag?>) : ViewModel() {

    private val _amount = MutableStateFlow(Amount(value = ""))
    val amount: StateFlow<Amount> = _amount
    fun changeAmount(amount: String) = _amount.update { it.copy(value = amount) }

    private val _cardState: MutableStateFlow<CreditCardState> = MutableStateFlow(CreditCardState.StoppedSearching)
    val cardState: StateFlow<CreditCardState> = _cardState
    fun changeCardState(card: CreditCardState) = _cardState.update { card }

    private val uiOperationState = combine(amount, cardState) { amountValue, cardStateValue ->
        Pair(amountValue, cardStateValue)
    }
    val operationResult: StateFlow<MontCoinOperationState?> = nfc.flatMapLatest { tag ->
        uiOperationState.take(1).map { (amountValue, cardStateValue) ->
            Triple(tag, amountValue, cardStateValue)
        }
    }.transform { (tag, amountValue, cardStateValue) ->
        if (amountValue.isValid() && cardStateValue == CreditCardState.SearchingCard && tag != null) {
            val res = writeTransaction("user", amountValue.toInt())
            _showOperationResult.emit(true)
            emit(res)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)

    private val _showOperationResult = MutableStateFlow(false)
    val showOperationResult = _showOperationResult
    fun cleanOperationResult() = _showOperationResult.update { false }

    init {
        viewModelScope.launch {
            showOperationResult.collect {
                Log.e("OperationViewModel", "Show result: $it")
            }
        }
    }

    private val _isDoingOperation = MutableStateFlow(false)
    val isDoingOperation: StateFlow<Boolean> = _isDoingOperation


    private suspend fun writeTransaction(user: String, amount: Int): MontCoinOperationState {
        _isDoingOperation.emit(true)
        val goodTransaction = false
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