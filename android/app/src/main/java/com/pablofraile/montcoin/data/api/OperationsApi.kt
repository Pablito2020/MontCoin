package com.pablofraile.montcoin.data.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson

data class OperationToday(val positive_amount: Int, val negative_amount: Int, val hour: Int)

object OperationsApi {
    private const val API_URL = "http://192.168.2.29:8000"

    suspend fun getOperationsToday(): Result<List<OperationToday>> {
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val response = client.get("${API_URL}/operations/today")
        if (!response.status.isSuccess())
            return Result.failure(Exception("Error getting operations today"))
        return Result.success(response.body())
    }

}