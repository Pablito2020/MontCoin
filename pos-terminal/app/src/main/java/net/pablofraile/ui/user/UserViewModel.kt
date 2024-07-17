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