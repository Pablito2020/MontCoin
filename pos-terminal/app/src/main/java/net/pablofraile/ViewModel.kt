package net.pablofraile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.pablofraile.data.card.CardRepository
import net.pablofraile.data.operations.OperationsRepository
import net.pablofraile.ui.common.Sensor

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(cardRepository: CardRepository, private val repo: OperationsRepository) :
    ViewModel() {

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Searching)
    val sensor: StateFlow<Sensor> = _sensor
    private fun changeCardState(card: Sensor) {
        _sensor.update { card }
    }

    fun searchDevices() = changeCardState(Sensor.Searching)
    private fun stopSearchingDevices() = changeCardState(Sensor.Stopped)


    val userId = cardRepository.observeUsersId().flatMapLatest { user ->
        sensor.take(1).map {
            Pair(user, it)
        }
    }.transform { (tag, sensor) ->
        if (tag.isSuccess && sensor == Sensor.Searching) {
            emit(tag.getOrThrow().value)
            stopSearchingDevices()
        } else {
            emit(null)
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    companion object {
        fun provideFactory(
            cardRepository: CardRepository,
            operationsRepository: OperationsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(cardRepository, operationsRepository) as T
                }
            }
    }

}
