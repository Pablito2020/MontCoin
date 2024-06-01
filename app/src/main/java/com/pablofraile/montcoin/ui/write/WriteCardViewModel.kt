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
        _selectedUser.value = user
    }
    fun clearSelectedUser() {
        _selectedUser.value = null
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users
    fun updateUsers() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            _isRefreshing.value = true
            val users = usersRepository.getUsers()
            if (users.isSuccess) _users.update{ users.getOrThrow() }
            else errorMessage.emit(users.exceptionOrNull()?.message ?: "Unknown Error")
            _isRefreshing.value = false
        }
    }


    init {
        updateUsers()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing

    val writeResult = selectedUser.transform {
        if (it != null) {
            val result = cardRepository.writeToCard(it)
            if (result.isFailure) {
                errorMessage.emit(
                    result.exceptionOrNull()!!.message ?: "Unknown Error"
                )
                _selectedUser.value = null
            } else {
                _selectedUser.value = null
                emit(Pair(result.getOrThrow(), Date.from(Instant.now())))
            }
        }
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
