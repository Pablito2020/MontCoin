package net.pablofraile.ui.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import net.pablofraile.data.operations.OperationsRepository
import net.pablofraile.data.users.UsersRepository
import net.pablofraile.model.Id
import net.pablofraile.model.Operations
import net.pablofraile.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Percentage {
    data object Empty : Percentage()
}

data class PercentageV(val income: Float, val negative: Float): Percentage() {
    init {
        Log.e("Percentage", "${income.toString()}, ${negative.toString()}")
        require(income + negative == 1f) { "Income and negative must sum 1" }
    }

    fun toList(): List<Float> = listOf(income, negative)
}

class UserViewModel(
    private val userId: Id,
    private val usersRepository: UsersRepository,
    private val operationsRepository: OperationsRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage
    fun cleanError() = _errorMessage.update { null }

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user

    private val _operations: MutableStateFlow<Operations> = MutableStateFlow(emptyList())
    val operations = _operations

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading

    private val _percentageFromOperations = _operations.map {
        val totalSum = it.sumOf { op -> if (op.amount.value < 0) op.amount.value * -1 else op.amount.value }
        if (it.isEmpty() || totalSum == 0) return@map Percentage.Empty
        val negative = it.sumOf { op -> if (op.amount.value < 0) -1 * op.amount.value else 0 }
        val positive = it.sumOf { op -> if (op.amount.value > 0) op.amount.value else 0 }
        PercentageV(
            income = positive.toFloat() / totalSum,
            negative = negative.toFloat() / totalSum,
        )
    }
    val percentage = _percentageFromOperations.stateIn(
        scope=viewModelScope,
        started=SharingStarted.Lazily,
        initialValue=null,
    )

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            fetchAll()
        }
    }

    private suspend fun fetchAll() {
        _isLoading.update { true }
        fetchUser { fetchOperations() }
        _isLoading.update { false }
    }

    private suspend fun fetchUser(onFetched: suspend () -> Unit) {
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