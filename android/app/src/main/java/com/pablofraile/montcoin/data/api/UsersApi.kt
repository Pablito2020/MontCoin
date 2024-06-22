package com.pablofraile.montcoin.data.api

class UserApi(
    val id: String,
    val name: String,
    val amount: Int,
    val operations_with_card: Int
)

class UsersApi(apiUrl: String, credentials: Credentials) : CommonApi(apiUrl, credentials) {

    suspend fun getUsers(): Result<List<UserApi>> {
        return get("/users")
    }

    suspend fun getUserById(id: String): Result<UserApi?> {
        return getOr404Null("/user/$id")
    }
}