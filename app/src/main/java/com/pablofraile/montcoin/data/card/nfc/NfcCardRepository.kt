package com.pablofraile.montcoin.data.card.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.utils.getParcelableCompatibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform

object NfcCardRepository : CardRepository {

    private val _intents: MutableSharedFlow<Intent?> = MutableSharedFlow()
    private val _tags: Flow<Tag?> = _intents.filterNotNull().map { getTagFromIntent(it) }
    private val _users: Flow<User> = _intents.filterNotNull().transform {
        // TODO Here we have to parse the tag!
        val userId = it.toUserId()
        if (userId.isSuccess) {
            val user = User(userId.getOrNull()!!, "Pablo Fraile", Amount("1000"))
            emit(user)
        }
    }

    override suspend fun writeToCard(user: User): Result<User> {
        val tag = _tags.first()
        return NfcTagWriter.write(tag, user.toNfcTag()).map { user }
    }

    override fun observeUsers(): Flow<User> = _users

    suspend fun send(intent: Intent?) = _intents.emit(intent)

    private fun getTagFromIntent(nfcIntent: Intent) = nfcIntent.getParcelableCompatibility(
        NfcAdapter.EXTRA_TAG,
        Tag::class.java
    )

}