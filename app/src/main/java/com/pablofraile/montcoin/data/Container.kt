package com.pablofraile.montcoin.data

import android.content.Context
import com.pablofraile.montcoin.data.operations.InMemoryOperationRepository
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.InMemoryUserRepo
import com.pablofraile.montcoin.data.users.UsersRepository

interface AppContainer {
    val usersRepository: UsersRepository
    val operationsRepository: OperationsRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val usersRepository: UsersRepository by lazy { InMemoryUserRepo() }

    override val operationsRepository: OperationsRepository by lazy { InMemoryOperationRepository() }

}