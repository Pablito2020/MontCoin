package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User
import kotlinx.coroutines.delay

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
                User(Id("1"), "Pablo Fraile Alonso", Amount(1000)),
                User(Id("2"), "Coto Croto Lamo", Amount(2000)),
                User(Id("3"), "Paujurado El Rey, The King", Amount(2000))
            )
        )
    }

}