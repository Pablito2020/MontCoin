package com.pablofraile.montcoin.ui.operation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import com.pablofraile.montcoin.nfc.ReadTag
import com.pablofraile.montcoin.nfc.Sensor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update


@OptIn(ExperimentalCoroutinesApi::class)
class OperationViewModel(nfc: Flow<ReadTag?>, private val repo: OperationsRepository) :
    ViewModel() {

    private val _amount = MutableStateFlow(Amount(value = ""))
    val amount: StateFlow<Amount> = _amount
    fun changeAmount(amount: String) = _amount.update { it.copy(value = amount) }

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Stopped)
    val sensor: StateFlow<Sensor> = _sensor
    private fun changeCardState(card: Sensor) = _sensor.update { card }
    fun searchDevices() = changeCardState(Sensor.Searching)
    fun stopSearchingDevices() = changeCardState(Sensor.Stopped)

    val operationResult = nfc.flatMapLatest { tag ->
        combine(amount, sensor, ::Pair).take(1).map { (amount, sensor) ->
            Triple(tag, amount, sensor)
        }
    }.transform { (tag, amount, sensor) ->
        if (amount.isValid() && sensor == Sensor.Searching && tag != null)
            emit(doOperation(tag, amount))
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    private val _isDoingOperation = MutableStateFlow(false)
    val isDoingOperation: StateFlow<Boolean> = _isDoingOperation

    private suspend fun doOperation(tag: ReadTag, amount: Amount): Result<Operation> {
        _isDoingOperation.emit(true)
        Log.d("OperationViewModel", "Doing operation: ${tag.readOperation} with amount $amount")
        val mockUser = Id("test")
        val result = repo.execute(WriteOperation(mockUser, amount))
        _isDoingOperation.emit(false)
        return result
    }

    companion object {
        fun provideFactory(
            nfc: Flow<ReadTag?>,
            operationsRepository: OperationsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationViewModel(nfc, operationsRepository) as T
                }
            }
    }

}