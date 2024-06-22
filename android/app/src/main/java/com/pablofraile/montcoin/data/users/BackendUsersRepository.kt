package com.pablofraile.montcoin.data.users

import com.pablofraile.montcoin.data.api.UsersApi
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User

class BackendUsersRepository : UsersRepository {
    override suspend fun getUserById(id: String): Result<User?> {
        return UsersApi.getUserById(id).map {
            it?.let {
                User(
                    id = Id(it.id),
                    name = it.name,
                    amount = Amount(it.amount),
                    numberOfOperations = it.operations_with_card
                )
            }
        }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return UsersApi.getUsers().map {
            it.map { userApi ->
                User(
                    id = Id(userApi.id),
                    name = userApi.name,
                    amount = Amount(userApi.amount),
                    numberOfOperations = userApi.operations_with_card
                )
            }
        }
    }
}