package com.pablofraile.montcoin.data

import android.content.Context
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.card.nfc.NfcCardRepository
import com.pablofraile.montcoin.data.operations.BackendOperationRepository
import com.pablofraile.montcoin.data.operations.InMemoryOperationRepository
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.InMemoryUserRepo
import com.pablofraile.montcoin.data.users.UsersRepository

interface AppContainer {
    val usersRepository: UsersRepository
    val operationsRepository: OperationsRepository
    val cardRepository: CardRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val usersRepository: UsersRepository by lazy { InMemoryUserRepo() }

    override val operationsRepository: OperationsRepository by lazy {
        BackendOperationRepository()
    }

    override val cardRepository by lazy { NfcCardRepository }

}