package com.cs6018.canvasexample.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ApiRepository(private val scope: CoroutineScope, private val apiService: ApiService) {

    val currentUserExploreFeed: MutableLiveData<List<DrawingResponse>> = MutableLiveData(listOf())

    var currentUserDrawingHistory: MutableLiveData<List<DrawingResponse>> =
        MutableLiveData(listOf())

    var activeDrawingInfo: MutableLiveData<DrawingResponse?> = MutableLiveData(null)

    var activeDrawingBackgroundImageReference: MutableLiveData<String?> = MutableLiveData(null)

    var activeDrawingTitle: MutableLiveData<String?> = MutableLiveData("Untitled")

    fun resetData() {
        currentUserExploreFeed.postValue(listOf())
        currentUserDrawingHistory.postValue(listOf())
        activeDrawingInfo.postValue(null)
        activeDrawingBackgroundImageReference.postValue(null)
        activeDrawingTitle.postValue("Untitled")
    }

    fun setActiveDrawingBackgroundImageReference(imageReference: String?) {
        activeDrawingBackgroundImageReference.postValue(imageReference)
    }

    suspend fun updateDrawingTitleById(title: String, thumbnail: String) {
        val drawingId = activeDrawingInfo.value?.id ?: 0
        apiService.updateDrawingById(
            drawingId,
            DrawingPost(
                activeDrawingInfo.value?.creatorId ?: "",
                title,
                activeDrawingInfo.value?.imagePath ?: "",
                thumbnail
            )
        )
        Log.d("ApiRepository", "updateDrawingTitleById: $title")
    }

    suspend fun postNewDrawing(creatorId: String, imagePath: String, thumbnail: String) {
        apiService.postNewDrawing(
            DrawingPost(
                creatorId, activeDrawingTitle.value ?: "Untitled", imagePath, thumbnail
            )
        )
        Log.d("ApiRepository", "postNewDrawing: ${activeDrawingTitle.value ?: "Untitled"}")
    }

    fun getCurrentUserDrawingHistory(userId: String) {
        scope.launch {
            val drawings = apiService.getCurrentUserDrawingHistory(userId)
            Log.d("ApiRepository", "getCurrentUserDrawingHistory: $drawings")
            currentUserDrawingHistory.postValue(drawings)
        }
    }

    suspend fun getCurrentUserExploreFeed(userId: String) {
        val drawings = apiService.getCurrentUserFeed(userId)
        Log.d("ApiRepository", "getCurrentUserExploreFeed: $drawings")
        currentUserExploreFeed.postValue(drawings)
    }

    fun setActiveDrawingInfoTitle(title: String) {
        activeDrawingTitle.postValue(title)
        Log.d("ApiRepository", "setActiveDrawingInfoTitle: $title")
    }

    fun setActiveDrawingInfoById(id: Int) {
        scope.launch {
            val drawings = apiService.getDrawingById(id)
            if (drawings.isEmpty()) {
                Log.d("ApiRepository", "setActiveDrawingInfoById: drawings is empty")
                activeDrawingInfo.postValue(null)
                setActiveDrawingInfoTitle("Untitled")
            } else {
                Log.d("ApiRepository", "setActiveDrawingInfoById: ${drawings[0]}")
                activeDrawingInfo.postValue(drawings[0])
                setActiveDrawingInfoTitle(drawings[0].title)
                setActiveDrawingBackgroundImageReference(drawings[0].imagePath)
            }
        }
    }

    fun deleteDrawingById(id: Int) {
        scope.launch {
            apiService.deleteDrawingById(id)
            Log.d("ApiRepository", "deleteDrawingById: $id")
        }
    }
}