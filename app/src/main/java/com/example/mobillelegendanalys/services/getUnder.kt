package org.example.mlanalis.services

import android.content.Context
import org.example.mlanalis.data.GetUnder
import org.example.mlanalis.data.ResponseUnder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.mlanalis.local.DataStoreManager

suspend fun fetchUnder(context: Context): List<GetUnder> {
    try {
        val localData = DataStoreManager.getUnder(context)
        if (!localData.isNullOrEmpty()) {
            println("Ambil dari local")
            val response: ResponseUnder = Json.decodeFromString(localData)
            return response.data
        }
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val responseText = client.get("https://api-ml-drab.vercel.app/under").body<String>()
        DataStoreManager.saveUnder(context, responseText)
        val response: ResponseUnder = Json.decodeFromString(responseText)
        return response.data
    } catch (e: Exception) {
        println("Error fetching data: ${e.message}")
        return emptyList()
    }
}