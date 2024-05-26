package com.pablofraile.montcoin.ui.operations

import androidx.compose.foundation.shape.CornerSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablofraile.montcoin.data.operations.OperationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OperationsViewModel(val operationsRepo: OperationsRepository) : ViewModel() {
    private var currentFetched = INIT_FETCHED

    init {
        loadMoreOperations()
    }

    val operations = operationsRepo.observeOperations()
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = emptyList())

    suspend fun refreshOperations() {
        currentFetched = 0
        loadMoreOperationsAsync()
    }

    fun loadMoreOperations() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            loadMoreOperationsAsync()
        }
    }

    private suspend fun loadMoreOperationsAsync() {
        operationsRepo.fetchOperations(
            currentFetched = currentFetched,
            toFetch = currentFetched + FETCH_BATCHING
        )
        currentFetched += FETCH_BATCHING
    }


    companion object {
        fun provideFactory(operationsRepo: OperationsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationsViewModel(operationsRepo = operationsRepo) as T
                }
            }

        const val INIT_FETCHED = 0
        const val FETCH_BATCHING = 10
    }

}
