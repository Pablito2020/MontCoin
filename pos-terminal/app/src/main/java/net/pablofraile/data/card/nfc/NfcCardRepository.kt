package net.pablofraile.data.card.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import net.pablofraile.data.card.CardRepository
import net.pablofraile.model.Amount
import net.pablofraile.model.Id
import net.pablofraile.model.User
import net.pablofraile.utils.getParcelableCompatibility
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
    private val _users: Flow<Result<Id>> = _intents.filterNotNull().map {
        // TODO Here we have to parse the tag!
        it.toUserId()
    }

    override suspend fun writeToCard(user: User): Result<User> {
        val tag = _tags.first()
        return NfcTagWriter.write(tag, user.toNfcTag()).map { user }
    }

    override fun observeUsersId(): Flow<Result<Id>> = _users

    suspend fun send(intent: Intent?) = _intents.emit(intent)

    private fun getTagFromIntent(nfcIntent: Intent) = nfcIntent.getParcelableCompatibility(
        NfcAdapter.EXTRA_TAG,
        Tag::class.java
    )

}