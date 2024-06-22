package com.pablofraile.montcoin.data.api

class UserApi(
    val id: String,
    val name: String,
    val amount: Int,
    val operations_with_card: Int
)

object UsersApi {

    suspend fun getUsers(): Result<List<UserApi>> {
        return CommonApi.get("/users")
    }

    suspend fun getUserById(id: String): Result<UserApi?> {
        return CommonApi.getOr404Null("/user/$id")
    }
}