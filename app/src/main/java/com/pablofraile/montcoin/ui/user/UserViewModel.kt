package com.pablofraile.montcoin.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userId: Id,
    private val usersRepository: UsersRepository,
    private val operationsRepository: OperationsRepository
): ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage
    fun cleanError() = _errorMessage.update { null }

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user

    private val _operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())
    val operations = _operations

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading

    init { onRefresh() }

    fun onRefresh() {
        viewModelScope.launch {
            fetchAll()
        }
    }

    private suspend fun fetchAll() {
        _isLoading.update { true }
        fetchUser() { fetchOperations() }
        _isLoading.update { false }
    }

    private suspend fun fetchUser(onFetched: suspend () -> Unit){
        val user = this.usersRepository.getUserById(userId.value)
        if (user.isSuccess) {
            _user.update { user.getOrNull() }
            onFetched()
        } else {
            _errorMessage.update { user.exceptionOrNull()?.message }
        }
    }

    private suspend fun fetchOperations() {
        val operations = this.operationsRepository.getOperationsFor(userId)
        if (operations.isSuccess) _operations.update { operations.getOrNull() ?: emptyList() }
        else _errorMessage.update { operations.exceptionOrNull()?.message }
    }


    companion object {
        fun provideFactory(
            userId: Id,
            usersRepository: UsersRepository,
            operationsRepository: OperationsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(userId, usersRepository, operationsRepository) as T
                }
            }
    }
}