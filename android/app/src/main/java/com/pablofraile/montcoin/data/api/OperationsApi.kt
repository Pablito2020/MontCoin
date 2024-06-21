package com.pablofraile.montcoin.data.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

object OperationsApi {
    private const val API_URL = "http://192.168.2.29:8000"

    suspend fun getOperationsToday() {
        val client = HttpClient(OkHttp)
        val response = client.get("${API_URL}/operations/today")
        Log.e("Operations Api Client", response.bodyAsText())
    }

}