package com.pablofraile.montcoin.ui.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.Sensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class WriteCardViewModel(
    private val cardRepository: CardRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _writeResult = MutableSharedFlow<Pair<User, Date>>()
    val writeResult = _writeResult

    private var writingJob: Job? = null

    private val _sensor: MutableStateFlow<Sensor> = MutableStateFlow(Sensor.Stopped)
    val sensor = _sensor

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser = _selectedUser
    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun clearSelectedUser(user: User) {
        _selectedUser.value = null
    }
    val users = usersRepository.observeUsers().stateIn(
        viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList()
    )
    suspend fun updateUsers() = usersRepository.getUsers()

    private fun changeCardState(card: Sensor) {
        when (card) {
            Sensor.Stopped -> writingJob?.cancel()
            Sensor.Searching -> {
                val scope = CoroutineScope(Dispatchers.IO)
                writingJob = scope.launch { writeCard() }
            }
        }
        _sensor.update { card }
    }

    fun startSearching() = changeCardState(Sensor.Searching)
    fun stopSearching() = changeCardState(Sensor.Stopped)

    private suspend fun writeCard() {
        val user = selectedUser.value ?: return
        val result = cardRepository.writeToCard(user)
        if (result.isFailure) errorMessage.emit(
            result.exceptionOrNull()!!.message ?: "Unknown Error"
        )
        else _writeResult.emit(Pair(result.getOrThrow(), Date.from(Instant.now())))
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    companion object {
        fun provideFactory(
            cardRepository: CardRepository,
            usersRepository: UsersRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WriteCardViewModel(cardRepository, usersRepository) as T
                }
            }
    }
}
