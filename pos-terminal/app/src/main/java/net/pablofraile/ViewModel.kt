package net.pablofraile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import net.pablofraile.data.card.CardRepository
import net.pablofraile.data.operations.OperationsRepository
import net.pablofraile.data.users.UsersRepository
import net.pablofraile.model.Id
import net.pablofraile.model.Operations
import net.pablofraile.model.User
import net.pablofraile.ui.common.Sensor
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pablofraile.ui.user.Percentage
import net.pablofraile.ui.user.PercentageV

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    cardRepository: CardRepository,
    private val repo: OperationsRepository,
    private val usersRepository: UsersRepository,
) :
    ViewModel() {

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Searching)
    val sensor: StateFlow<Sensor> = _sensor
    private fun changeCardState(card: Sensor) {
        _sensor.update { card }
    }

    private val _lastInteraction: MutableStateFlow<Long> = MutableStateFlow(System.currentTimeMillis())
    val lastInteraction: StateFlow<Long> = _lastInteraction
    fun updateLastInteraction() {
        Log.e("DEBUGING", "UPdated Last Inter")
        viewModelScope.launch {
            _lastInteraction.emit(System.currentTimeMillis())
        }
    }

    fun searchDevices() = changeCardState(Sensor.Searching)
    private fun stopSearchingDevices() = changeCardState(Sensor.Stopped)


    val userId = cardRepository.observeUsersId().flatMapLatest { user ->
        sensor.take(1).map {
            Pair(user, it)
        }
    }.transform { (tag, sensor) ->
        if (tag.isSuccess && sensor == Sensor.Searching) {
            val id = tag.getOrThrow().value
            fetchAll(id)
            emit(id)
            updateLastInteraction()
            stopSearchingDevices()
        } else {
            emit(null)
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily)


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage
    fun cleanError() = _errorMessage.update { null }

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user

    private val _operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())
    val operations = _operations

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading

    private val _percentageFromOperations = _operations.map {
        val totalSum =
            it.sumOf { op -> if (op.amount.value < 0) op.amount.value * -1 else op.amount.value }
        if (it.isEmpty() || totalSum == 0) return@map Percentage.Empty
        val negative = it.sumOf { op -> if (op.amount.value < 0) -1 * op.amount.value else 0 }
        val positive = it.sumOf { op -> if (op.amount.value > 0) op.amount.value else 0 }
        PercentageV(
            income = positive.toFloat() / totalSum,
            negative = negative.toFloat() / totalSum,
        )
    }
    val percentage = _percentageFromOperations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    private suspend fun fetchAll(userId: String) {
        _isLoading.update { true }
        fetchUser(userId) { fetchOperations(it) }
        _isLoading.update { false }
    }

    private suspend fun fetchUser(userId: String, onFetched: suspend (String) -> Unit) {
        val user = this.usersRepository.getUserById(userId)
        if (user.isSuccess) {
            _user.update { user.getOrNull() }
            onFetched(userId)
        } else {
            _errorMessage.update { user.exceptionOrNull()?.message }
        }
    }

    private suspend fun fetchOperations(userId: String) {
        val operations = this.repo.getOperationsFor(Id(userId))
        if (operations.isSuccess) _operations.update { operations.getOrNull() ?: emptyList() }
        else _errorMessage.update { operations.exceptionOrNull()?.message }
    }


    companion object {
        fun provideFactory(
            cardRepository: CardRepository,
            operationsRepository: OperationsRepository,
            usersRepository: UsersRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(cardRepository, operationsRepository, usersRepository) as T
                }
            }
    }

}
