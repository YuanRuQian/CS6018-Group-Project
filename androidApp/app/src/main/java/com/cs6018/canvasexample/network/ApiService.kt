package com.cs6018.canvasexample.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class DrawingResponse(
    val id: Int,
    val creatorId: String,
    val title: String,
    val lastModifiedDate: Long,
    val createdDate: Long,
    val imagePath: String,
)


@Serializable
data class DrawingPost(
    val creatorId: String,
    val title: String,
    val imagePath: String
)

class ApiService {
    // FIXME: 10.0.2.2 is the localhost alias for the Android emulator, but it doesn't work on real devices
    private val URL_BASE = "http://10.0.2.2:8080"

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Resources)
    }

    @Throws(Exception::class)
    suspend fun getAllDrawings(): List<DrawingResponse> {
        return httpClient.get("$URL_BASE/drawings").body()
    }

    @Throws(Exception::class)
    suspend fun getCurrentUserDrawingHistory(userId: String): List<DrawingResponse> {
        return httpClient.get("$URL_BASE/drawings/$userId").body()
    }

    @Throws(Exception::class)
    suspend fun postNewDrawing(drawing: DrawingPost) {
        Log.d("ApiService", "postNewDrawing: $drawing")
        return httpClient.post("$URL_BASE/drawings/create") {
            contentType(ContentType.Application.Json)
            setBody(drawing)
        }.body()
    }
}