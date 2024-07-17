package com.pablofraile.montcoin.ui.operation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.WriteOperation
import com.pablofraile.montcoin.model.isValidAmount
import com.pablofraile.montcoin.model.toAmount
import com.pablofraile.montcoin.ui.common.Sensor
import com.pablofraile.montcoin.ui.common.toChartData
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
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
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class OperationViewModel(cardRepository: CardRepository, private val repo: OperationsRepository) :
    ViewModel() {

    init {
        onRefresh()
    }

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    fun changeAmount(amount: String) {
        changeCardState(Sensor.Stopped)
        _amount.update { amount }
    }

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Stopped)
    val sensor: StateFlow<Sensor> = _sensor
    private fun changeCardState(card: Sensor) {
        if (!_amount.value.isValidAmount() && card == Sensor.Searching) return
        _sensor.update { card }
    }

    private val _shouldFail: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val shouldFailIfNotEnoughMoney: StateFlow<Boolean> = _shouldFail
    fun changeFailCondition(newValue: Boolean) {
        //val newValue = !_shouldFail.value
        _shouldFail.value = newValue
    }

    fun searchDevices() = changeCardState(Sensor.Searching)
    fun stopSearchingDevices() = changeCardState(Sensor.Stopped)

    val operationResult = cardRepository.observeUsersId().flatMapLatest { user ->
        combine(amount, sensor, ::Pair).take(1).map { (amount, sensor) ->
            Triple(user, amount, sensor)
        }
    }.transform { (tag, value, sensor) ->
        if (value.isValidAmount() && sensor == Sensor.Searching) {
            val result = doOperation(tag, value.toAmount().getOrThrow())
            if (result.isFailure) _errorMessage.emit(result.exceptionOrNull()?.message)
            else emit(result.getOrThrow())
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    fun cleanError() = _errorMessage.update { null }

    val modelProducer = CartesianChartModelProducer.build()
    fun onRefresh() {
        viewModelScope.launch {
            val result = repo.getOperationsForToday()
            if (result.isSuccess) modelProducer.runTransaction {
                val resultStats = result.getOrThrow()
                val data = resultStats.toChartData()
                Log.e("OperationViewModel", "onRefresh: $data")
                lineSeries {
                    if (data.income.isEmpty()) {
                        series(x = listOf(0), y = listOf(0))
                    } else {
                        series(x = data.income.map { it.second }, y = data.income.map { it.first })
                    }
                    if (data.expenses.isEmpty()) {
                        series(x = listOf(0), y = listOf(0))
                    } else {
                        series(
                            x = data.expenses.map { it.second },
                            y = data.expenses.map { it.first })
                    }
                }
            }.await()
            else _errorMessage.emit(result.exceptionOrNull()?.message)
        }
    }

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
        val result = repo.execute(
            WriteOperation(
                userId,
                amount,
                shouldFailIfNotEnough = _shouldFail.value,
                withCard = true
            )
        )
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