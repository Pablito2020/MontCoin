package com.pablofraile.montcoin.ui.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.model.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val BATCHING = 20

class OperationsViewModel(val operationsRepo: OperationsRepository) : ViewModel() {

    private val _fetchedPages = MutableStateFlow(0)
    private val _op: MutableStateFlow<List<Operation>> = MutableStateFlow(emptyList())
    val operations = _op

    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages = _errorMessages

    private val _initialLoading = MutableStateFlow(true)
    val initialLoading = _initialLoading

    suspend fun fetchFirstPage() {
        initialLoading.update { true }
        reload()
        initialLoading.update { false }
    }

    suspend fun reload() {
        _fetchedPages.update { 0 }
        fetchNextPage()
    }

    suspend fun fetchNextPage() {
        val page = _fetchedPages.value
        val newOperations = operationsRepo.getOperations(page = page, size = BATCHING)
        if (newOperations.isSuccess) {
            _fetchedPages.update { it + 1 }
            if (page == 0) _op.update { newOperations.getOrNull()!! }
            else _op.update { it + newOperations.getOrNull()!! }
        } else _errorMessages.update { newOperations.exceptionOrNull()?.message }
    }

    init {
        viewModelScope.launch {
            fetchFirstPage()
        }
    }

    companion object {
        fun provideFactory(operationsRepo: OperationsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationsViewModel(operationsRepo = operationsRepo) as T
                }
            }
    }

}
