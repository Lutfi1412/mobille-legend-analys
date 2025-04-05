package org.example.mlanalis.services

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.mlanalis.data.GetOP
import org.example.mlanalis.data.ResponseOP
import org.example.mlanalis.local.DataStoreManager

suspend fun fetchOP(context: Context): List<GetOP> {
    try {
        val localData = DataStoreManager.getOP(context)
        if (!localData.isNullOrEmpty()) {
            println("Ambil dari local")
            val response: ResponseOP = Json.decodeFromString(localData)
            return response.data
        }
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val responseText = client.get("https://api-ml-drab.vercel.app/op").body<String>()
        DataStoreManager.saveOP(context, responseText)
        val response: ResponseOP = Json.decodeFromString(responseText)
        return response.data
    } catch (e: Exception) {
        println("Error fetching data: ${e.message}")
        return emptyList()
    }
}
