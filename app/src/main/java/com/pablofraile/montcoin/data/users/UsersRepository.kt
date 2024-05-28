package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUserById(id: String): Result<User?>
    suspend fun getUsers(): Result<List<User>>
    fun observeUsers(): Flow<List<User>>
}