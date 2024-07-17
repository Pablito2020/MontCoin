package net.pablofraile.data

import android.content.Context
import net.pablofraile.R
import net.pablofraile.data.api.Credentials
import net.pablofraile.data.api.OperationsApi
import net.pablofraile.data.api.UsersApi
import net.pablofraile.data.card.CardRepository
import net.pablofraile.data.card.nfc.NfcCardRepository
import net.pablofraile.data.operations.BackendOperationRepository
import net.pablofraile.data.operations.OperationsRepository
import net.pablofraile.data.users.BackendUsersRepository
import net.pablofraile.data.users.UsersRepository

interface AppContainer {
    val credentials: Credentials
    val usersRepository: UsersRepository
    val operationsRepository: OperationsRepository
    val cardRepository: CardRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val apiUrl = "https://api.esplaiepis.org"

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