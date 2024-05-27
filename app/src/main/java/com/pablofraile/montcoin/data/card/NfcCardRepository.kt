package com.pablofraile.montcoin.data.card

import android.nfc.Tag
import android.util.Log
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object NfcCardRepository : CardRepository {

    private val _users: MutableSharedFlow<User> = MutableSharedFlow()

    override suspend fun writeToCard(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override fun observeUsers(): Flow<User> = _users

    suspend fun send(tag: Tag?) = tag?.let {
        Log.e("NFC Card Repository: ", "Readed Tag ${tag.id}")
        // TODO: Here we have to parse the tag!
        val mockUser = User(Id("1"), "Pablo Fraile", Amount("1000"))
        _users.emit(mockUser)
    }


}