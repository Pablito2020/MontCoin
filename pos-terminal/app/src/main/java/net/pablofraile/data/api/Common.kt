package net.pablofraile.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson

open class CommonApi(val apiUrl: String, val credentials: Credentials) {

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend inline fun <reified T>get(uri: String): Result<T> {
        try {
            val response = client.get("$apiUrl$uri")
            if (!response.status.isSuccess())
                return Result.failure(Exception("Error getting $uri"))
            return Result.success(response.body())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    suspend inline fun <reified T>getOr404Null(uri: String): Result<T?> {
        try {
            val response = client.get("$apiUrl$uri")
            if (response.status.value == 404)
                return Result.success(null)
            if (!response.status.isSuccess())
                return Result.failure(Exception("Error getting $uri"))
            return Result.success(response.body())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend inline fun <reified T, reified R>post(uri: String, body: R): Result<T> {
        try {
            val response = client.post("$apiUrl$uri") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("Error getting $uri"))
            }
            return Result.success(response.body())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}