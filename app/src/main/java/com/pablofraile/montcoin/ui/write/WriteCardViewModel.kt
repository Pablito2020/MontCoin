package com.pablofraile.montcoin.ui.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.Sensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WriteCardViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _writeResult = MutableSharedFlow<Result<User>>()
    val writeResult = _writeResult

    private var writingJob: Job? = null

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Stopped)
    val sensor = _sensor
    private fun changeCardState(card: Sensor) {
        when(card) {
            Sensor.Stopped -> writingJob?.cancel()
            Sensor.Searching -> {
                val scope = CoroutineScope(Dispatchers.IO)
                writingJob = scope.launch { writeCard()}
            }
        }
        _sensor.update { card }
    }
    fun startSearching() = changeCardState(Sensor.Searching)
    fun stopSearching() = changeCardState(Sensor.Stopped)

    private suspend fun writeCard() {
        val user = User(Id("RealId1"), "Pablo Fraile", Amount("1000"))
        val result = cardRepository.writeToCard(user)
        _writeResult.emit(result)
    }

    companion object {
        fun provideFactory(
            cardRepository: CardRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WriteCardViewModel(cardRepository) as T
                }
            }
    }
}
