package net.pablofraile.data.users

import net.pablofraile.data.api.UsersApi
import net.pablofraile.model.Amount
import net.pablofraile.model.Id
import net.pablofraile.model.User

class BackendUsersRepository(private val usersApi: UsersApi) : UsersRepository {
    override suspend fun getUserById(id: String): Result<User?> {
        return usersApi.getUserById(id).map {
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
        return usersApi.getUsers().map {
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