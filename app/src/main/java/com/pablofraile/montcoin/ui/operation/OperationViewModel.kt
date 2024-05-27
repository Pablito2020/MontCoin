package com.pablofraile.montcoin.ui.operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import com.pablofraile.montcoin.ui.common.Sensor
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class OperationViewModel(cardRepository: CardRepository, private val repo: OperationsRepository) :
    ViewModel() {

    private val _amount = MutableStateFlow(Amount(value = ""))
    val amount: StateFlow<Amount> = _amount
    fun changeAmount(amount: String) {
        changeCardState(Sensor.Stopped)
        _amount.update { it.copy(value = amount) }
    }

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Stopped)
    val sensor: StateFlow<Sensor> = _sensor
    private fun changeCardState(card: Sensor) {
        if (!_amount.value.isValid() && card == Sensor.Searching) return
        _sensor.update { card }
    }

    fun searchDevices() = changeCardState(Sensor.Searching)
    fun stopSearchingDevices() = changeCardState(Sensor.Stopped)

    val operationResult = cardRepository.observeUsersId().flatMapLatest { user ->
        combine(amount, sensor, ::Pair).take(1).map { (amount, sensor) ->
            Triple(user, amount, sensor)
        }
    }.transform { (tag, amount, sensor) ->
        if (amount.isValid() && sensor == Sensor.Searching) {
            val result = doOperation(tag, amount)
            if (result.isFailure) _errorMessage.emit(
                result.exceptionOrNull()?.message ?: "Unknown Error"
            )
            else emit(result.getOrThrow())
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    fun cleanError() = _errorMessage.update { null }

    private val _isDoingOperation = MutableStateFlow(false)
    val isDoingOperation: StateFlow<Boolean> = _isDoingOperation

    private suspend fun doOperation(userId: Result<Id>, amount: Amount): Result<Operation> {
        return userId.fold(
            onSuccess = { doOperation(it, amount) },
            onFailure = { Result.failure(it) }
        )
    }

    private suspend fun doOperation(userId: Id, amount: Amount): Result<Operation> {
        _isDoingOperation.emit(true)
        val result = repo.execute(WriteOperation(userId, amount))
        _isDoingOperation.emit(false)
        return result
    }

    companion object {
        fun provideFactory(
            cardRepository: CardRepository,
            operationsRepository: OperationsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationViewModel(cardRepository, operationsRepository) as T
                }
            }
    }

}