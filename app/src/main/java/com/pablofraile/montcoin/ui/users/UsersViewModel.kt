package com.pablofraile.montcoin.ui.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _order = MutableStateFlow(Order.UserName)
    val order: StateFlow<Order> = _order
    fun setOrder(order: Order) {
        _order.update { order }
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())

    val users: StateFlow<List<User>> =
        _users.combine(_order) { users, order -> users to order }.map { (users, order) ->
            when (order) {
                Order.UserName -> users.sortedBy { user -> user.name }
                Order.Amount -> users.sortedBy { user -> user.amount.value }
                Order.NumberOperationsAscendant -> users.sortedBy { user -> user.numberOfOperations }
                Order.NumberOperationsDescendant -> users.sortedByDescending { user -> user.numberOfOperations }
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, emptyList())

    private val _errors = MutableSharedFlow<String>()
    val errors = _errors

    private val _isLoadingUsers = MutableStateFlow(true)
    val isLoadingUsers: StateFlow<Boolean> = _isLoadingUsers

    init {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            fetchUsers()
            _isLoadingUsers.update { false }
        }
    }

    suspend fun fetchUsers() {
        val users = usersRepository.getUsers()
        if (users.isSuccess) _users.emit(users.getOrThrow())
        else _errors.emit("Error fetching users: ${users.exceptionOrNull()?.message}")
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