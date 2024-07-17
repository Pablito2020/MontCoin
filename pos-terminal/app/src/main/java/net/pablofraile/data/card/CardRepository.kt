package net.pablofraile.data.card

import net.pablofraile.model.Id
import net.pablofraile.model.User
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun writeToCard(user: User): Result<User>
    fun observeUsersId(): Flow<Result<Id>>
}