package com.pablofraile.montcoin.data.card

import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun writeToCard(user: User): Result<User>
    fun observeUsers(): Flow<Result<User>>
}