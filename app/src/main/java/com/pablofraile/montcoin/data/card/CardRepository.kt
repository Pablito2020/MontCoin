package com.pablofraile.montcoin.data.card

import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun writeToCard(user: User): Result<User>
    fun observeUsersId(): Flow<Result<Id>>
}