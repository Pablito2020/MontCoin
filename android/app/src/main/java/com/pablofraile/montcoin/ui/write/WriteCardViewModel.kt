package com.pablofraile.montcoin.ui.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class WriteCardViewModel(
    private val cardRepository: CardRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser = _selectedUser
    fun selectUser(user: User) {
        if (_selectedUser.value == user) _selectedUser.update { null }
        else _selectedUser.update { user }
    }

    private val _isWriting = MutableStateFlow(false)
    fun startWriting() {
        _isWriting.update { true }
    }
    fun stopWriting() {
        _isWriting.update { false }
    }
    val isWriting = _isWriting

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users
    fun updateUsers() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            _isRefreshing.update { true }
            val users = usersRepository.getUsers()
            if (users.isSuccess) _users.update { users.getOrThrow() }
            else errorMessage.emit(users.exceptionOrNull()?.message ?: "Unknown Error")
            _isRefreshing.update { false }
        }
    }

    init {
        updateUsers()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing

    val writeResult = combine(selectedUser, isWriting, ::Pair).transform {(user, isWriting) ->
        if (user != null && isWriting) {
            val result = cardRepository.writeToCard(user)
            _isWriting.update { false }
            if (result.isFailure) {
                errorMessage.emit(
                    result.exceptionOrNull()!!.message ?: "Unknown Error"
                )
            } else {
                emit(Pair(result.getOrThrow(), Date.from(Instant.now())))
            }
        }
    }

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.update { null }
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
