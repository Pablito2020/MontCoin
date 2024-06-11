package com.pablofraile.montcoin.ui.bulk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.BulkOperation
import com.pablofraile.montcoin.model.BulkOperationResult
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.model.toAmount
import com.pablofraile.montcoin.ui.common.UserSelectable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BulkOperationViewModel(
    private val usersRepository: UsersRepository,
    private val operationsRepository: OperationsRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserSelectable>>(emptyList())
    val users = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _errors = MutableStateFlow<String?>(null)
    val errors = _errors
    fun cleanErrors() {
        _errors.update { null }
        fetchUsers()
    }

    private val _correctOperation = MutableStateFlow<BulkOperationResult?>(null)
    val correctOperation = _correctOperation
    fun cleanCorrectOperation() {
        _correctOperation.update { null }
    }

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    fun changeAmount(amount: String) {
        _amount.update { amount }
    }

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            fetchUsersAsync()
        }
    }

    private suspend fun fetchUsersAsync() {
        _isLoading.update { true }
        _errors.update { null }
        val users = usersRepository.getUsers()
        if (users.isSuccess) {
            _users.emit(users.getOrThrow().map { user ->
                val userSelectable = _users.value.find { it.user.id == user.id }
                userSelectable ?: UserSelectable(user, false)
            }.sortedBy { it.user.name })
        } else {
            _errors.emit("Error fetching users: ${users.exceptionOrNull()?.message}")
        }
        _isLoading.update { false }
    }

    fun onSelectedUser(user: User) {
        _users.update { users ->
            users.map {
                if (it.user.id == user.id) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        }
    }

    fun toggleAllUsers() {
        _users.update { users ->
            val allSelected = users.all { it.isSelected }
            users.map { it.copy(isSelected = !allSelected) }
        }
    }

    fun makeOperation() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            operate()
        }
    }

    private suspend fun operate() {
        val selectedUsers = _users.value.filter { it.isSelected }.map { it.user }
        val amountValue = _amount.value.toAmount()
        if (selectedUsers.isEmpty() || amountValue.isFailure) return
        val operations = BulkOperation(
            users = selectedUsers.map { it.id },
            amount = amountValue.getOrThrow()
        )
        _isLoading.emit(true)
        val result = operationsRepository.execute(operations)
        _isLoading.emit(false)
        if (result.isFailure) {
            _errors.emit(result.exceptionOrNull()?.message)
        } else {
            _correctOperation.emit(result.getOrThrow())
        }
    }


    companion object {
        fun provideFactory(
            usersRepository: UsersRepository,
            operationsRepository: OperationsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BulkOperationViewModel(usersRepository, operationsRepository) as T
                }
            }
    }

}