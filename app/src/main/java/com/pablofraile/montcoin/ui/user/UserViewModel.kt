package com.pablofraile.montcoin.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    val userId: Id,
    val usersRepository: UsersRepository,
    val operationsRepository: OperationsRepository
): ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user

    val isInitialLoading = _user.combine(_errorMessage) { user, errorMessage ->
        user == null && errorMessage == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    init {
        viewModelScope.launch {
            fetchUser()
        }
    }

    private suspend fun fetchUser() {
        val user = this.usersRepository.getUserById(userId.value)
        if (user.isSuccess) _user.update { user.getOrNull() }
        else _errorMessage.update { user.exceptionOrNull()?.message }
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