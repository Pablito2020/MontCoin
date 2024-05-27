package com.pablofraile.montcoin.ui.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.Sensor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class WriteCardViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _writeResult = MutableSharedFlow<Result<User>>()
    val writeResult = _writeResult

    private val _isWriting = MutableStateFlow(false)
    val isWriting = _isWriting

    private val _sensor = MutableStateFlow(Sensor.Searching)
    val sensor = _sensor

    suspend fun writeCard() {
        _isWriting.value = true
        val user = User(Id("1"), "Pablo Fraile", Amount("1000"))
        val result = cardRepository.writeToCard(user)
        _writeResult.emit(result)
        _isWriting.value = false
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
