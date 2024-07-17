package net.pablofraile.data.users

import net.pablofraile.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUserById(id: String): Result<User?>
    suspend fun getUsers(): Result<List<User>>
}