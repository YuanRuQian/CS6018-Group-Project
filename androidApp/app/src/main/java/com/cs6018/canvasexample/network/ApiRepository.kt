package com.cs6018.canvasexample.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ApiRepository(private val scope: CoroutineScope, private val apiService: ApiService) {

    var currentUserDrawingHistory: MutableLiveData<List<DrawingResponse>> =
        MutableLiveData(listOf())

    var activeDrawingInfo: MutableLiveData<DrawingResponse?> = MutableLiveData(null)

    var activeDrawingTitle: MutableLiveData<String?> = MutableLiveData("Untitled")

    suspend fun updateDrawingTitleById(title: String) {
        val drawingId = activeDrawingInfo.value?.id ?: 0
        apiService.updateDrawingById(
            drawingId,
            DrawingPost(
                activeDrawingInfo.value?.creatorId ?: "",
                title,
                activeDrawingInfo.value?.imagePath ?: ""
            )
        )
        Log.d("ApiRepository", "updateDrawingTitleById: $title")
    }

    suspend fun postNewDrawing(creatorId: String, imagePath: String) {
        apiService.postNewDrawing(
            DrawingPost(
                creatorId, activeDrawingTitle.value ?: "Untitled", imagePath
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