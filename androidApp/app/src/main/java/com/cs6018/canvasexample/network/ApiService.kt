package com.cs6018.canvasexample.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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
    var title: String,
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
        Log.d("ApiService", "getAllDrawings: ")
        return httpClient.get("$URL_BASE/drawings").body()
    }

    @Throws(Exception::class)
    suspend fun getCurrentUserDrawingHistory(userId: String): List<DrawingResponse> {
        Log.d("ApiService", "getCurrentUserDrawingHistory: $userId")
        return httpClient.get("$URL_BASE/drawings/user/$userId/history").body()
    }

    @Throws(Exception::class)
    suspend fun getCurrentUserFeed(userId: String): List<DrawingResponse> {
        Log.d("ApiService", "getCurrentUserFeed: $userId")
        return httpClient.get("$URL_BASE/drawings/user/$userId/feed").body()
    }

    @Throws(Exception::class)
    suspend fun postNewDrawing(drawing: DrawingPost) {
        Log.d("ApiService", "postNewDrawing: $drawing")
        return httpClient.post("$URL_BASE/drawings/create") {
            contentType(ContentType.Application.Json)
            setBody(drawing)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getDrawingById(drawingId: Int) : List<DrawingResponse> {
        Log.d("ApiService", "getDrawingById: $drawingId")
        return httpClient.get("$URL_BASE/drawings/drawing/$drawingId").body()
    }

    @Throws(Exception::class)
    suspend fun updateDrawingById(drawingId: Int, drawing: DrawingPost) {
        Log.d("ApiService", "updateDrawingById: $drawingId, $drawing")
        return httpClient.put("$URL_BASE/drawings/drawing/$drawingId") {
            contentType(ContentType.Application.Json)
            setBody(drawing)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun deleteDrawingById(drawingId: Int) {
        Log.d("ApiService", "deleteDrawingById: $drawingId")
        return httpClient.delete("$URL_BASE/drawings/drawing/$drawingId").body()
    }
}