package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.data.Result
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUserById(id: String): Result<User?>
    fun observeUsers(): Flow<User>
    suspend fun fetchUsers(): Result<Nothing>
}