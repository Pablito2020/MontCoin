package com.pablofraile.montcoin.data

import android.content.Context
import com.pablofraile.montcoin.R
import com.pablofraile.montcoin.data.api.Credentials
import com.pablofraile.montcoin.data.api.OperationsApi
import com.pablofraile.montcoin.data.api.UsersApi
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.data.card.nfc.NfcCardRepository
import com.pablofraile.montcoin.data.operations.BackendOperationRepository
import com.pablofraile.montcoin.data.operations.OperationsRepository
import com.pablofraile.montcoin.data.users.BackendUsersRepository
import com.pablofraile.montcoin.data.users.UsersRepository

interface AppContainer {
    val credentials: Credentials
    val usersRepository: UsersRepository
    val operationsRepository: OperationsRepository
    val cardRepository: CardRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val apiUrl = "http://100.110.191.107:8000"

    override val credentials: Credentials by lazy {
        val privateKey =
            applicationContext.resources.openRawResource(R.raw.private_key).bufferedReader()
                .use { it.readText() }
        Credentials(privateKey = privateKey)
    }

    override val usersRepository: UsersRepository by lazy {
        BackendUsersRepository(
            UsersApi(
                apiUrl = apiUrl,
                credentials = credentials
            )
        )
    }

    override val operationsRepository: OperationsRepository by lazy {
        BackendOperationRepository(
            OperationsApi(
                apiUrl = apiUrl,
                credentials = credentials
            )
        )
    }

    override val cardRepository by lazy { NfcCardRepository }

}