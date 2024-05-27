package com.pablofraile.montcoin.data.card.nfc

import android.nfc.Tag
import com.pablofraile.montcoin.data.card.CardRepository
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

object NfcCardRepository : CardRepository {

    private val _tags: MutableSharedFlow<Tag?> = MutableSharedFlow()
    private val _users: Flow<User> = _tags.mapNotNull {
        // TODO Here we have to parse the tag!
        User(Id("1"), "Pablo Fraile", Amount("1000"))
    }

    override suspend fun writeToCard(user: User): Result<User> {
        val tag = _tags.first()
        return NfcTagWriter.write(tag, user.toNfcTag()).map { user }
    }

    override fun observeUsers(): Flow<User> = _users

    suspend fun send(tag: Tag?) = _tags.emit(tag)

}