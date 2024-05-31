package com.pablofraile.montcoin.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.UsersRepository
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.ui.users.UsersViewModel

class UserViewModel(
    val userId: Id,
    val usersRepository: UsersRepository,
    val operationsRepository: OperationsRepository
): ViewModel() {


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