package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.flow.Flow

class InMemoryUserRepo: UsersRepository {

    override suspend fun getUserById(id: String): Result<User?> {
        TODO("Not yet implemented")
    }

    override fun observeUsers(): Flow<User> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUsers(): Result<Unit> {
        TODO("Not yet implemented")
    }

}