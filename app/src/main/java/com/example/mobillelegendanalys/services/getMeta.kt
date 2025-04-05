package org.example.mlanalis.services

import android.content.Context
import org.example.mlanalis.data.GetMeta
import org.example.mlanalis.data.ResponseMeta
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

suspend fun fetchMeta(context: Context): List<GetMeta> {
    try {
        val localData = DataStoreManager.getMeta(context)
        if (!localData.isNullOrEmpty()) {
            println("Ambil dari local")
            val response: ResponseMeta = Json.decodeFromString(localData)
            return response.data
        }
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val responseText = client.get("https://api-ml-drab.vercel.app/meta").body<String>()
        DataStoreManager.saveMeta(context, responseText)
        val response: ResponseMeta = Json.decodeFromString(responseText)
        return response.data
    } catch (e: Exception) {
        println("Error fetching data: ${e.message}")
        return emptyList()
    }
}