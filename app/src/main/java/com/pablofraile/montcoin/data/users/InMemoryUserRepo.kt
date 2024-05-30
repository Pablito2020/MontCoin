package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InMemoryUserRepo : UsersRepository {

    override suspend fun getUserById(id: String): Result<User?> {
        val users =
            getUsers().getOrNull() ?: return Result.failure(IllegalStateException("No users found"))
        return Result.success(users.find { it.id.value == id })
    }

    override suspend fun getUsers(): Result<List<User>> {
        delay(2000)
        return Result.success(
            listOf(
                User(Id("1"), "Pablo", Amount(1000)),
                User(Id("2"), "Coto", Amount(2000)),
                User(Id("3"), "Pauju", Amount(2000))
            )
        )
    }

    override fun observeUsers(): Flow<List<User>> {
        return flow {
            emit(
                getUsers().getOrNull() ?: emptyList()
            )
        }
    }

}