package com.pablofraile.montcoin.ui.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _errors = MutableSharedFlow<String>()
    val errors = _errors

    private val _isLoadingUsers = MutableStateFlow(true)
    val isLoadingUsers: StateFlow<Boolean> = _isLoadingUsers

    init {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            updateUsers()
            _isLoadingUsers.value = false
        }
    }

    suspend fun updateUsers() {
        usersRepository.getUsers().fold(
            onSuccess = { _users.value = it },
            onFailure = { _errors.emit("Error updating users: ${it.message}") }
        )
    }

    companion object {
        fun provideFactory(
            usersRepository: UsersRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UsersViewModel(usersRepository) as T
                }
            }
    }

}