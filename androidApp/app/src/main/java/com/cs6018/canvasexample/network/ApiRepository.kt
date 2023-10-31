package com.cs6018.canvasexample.network

import android.util.Log
import androidx.lifecycle.MutableLiveData

class ApiRepository {

    val currentUserExploreFeed: MutableLiveData<List<UserDrawing>> = MutableLiveData(listOf())

    var currentUserDrawingHistory: MutableLiveData<List<UserDrawing>> =
        MutableLiveData(listOf())

    var activeDrawingInfo: MutableLiveData<UserDrawing?> = MutableLiveData(null)

    var activeDrawingTitle: MutableLiveData<String?> = MutableLiveData("Untitled")

    fun updateDrawingByDrawingId(title: String, thumbnail: String) {
        val drawingId = activeDrawingInfo.value?.id ?: ""
        val imagePath = activeDrawingInfo.value?.imagePath ?: ""
        updateDrawingInfo(drawingId, title, imagePath, thumbnail) {
            getCurrentUserDrawingHistory()
        }
        Log.d("ApiRepository", "updateDrawingTitleById: $title")
    }

    fun postNewDrawing(title: String, imagePath: String, thumbnail: String) {
        addNewDrawing(
            title,
            imagePath,
            thumbnail
        ) { getCurrentUserDrawingHistory() }
        Log.d("ApiRepository", "postNewDrawing: ${activeDrawingTitle.value ?: "Untitled"}")
    }

    fun getCurrentUserDrawingHistory() {
        val onSuccess = { drawings: List<UserDrawing> ->
            currentUserDrawingHistory.postValue(drawings)
        }
        getCurrentUserDrawings(onSuccess)
    }

    fun getCurrentUserExploreFeed() {
        val onSuccess = { drawings: List<UserDrawing> ->
            currentUserExploreFeed.postValue(drawings)
        }
        getPublicFeed(onSuccess)
    }

    fun setActiveDrawingInfoTitle(title: String) {
        activeDrawingTitle.postValue(title)
        Log.d("ApiRepository", "setActiveDrawingInfoTitle: $title")
    }

    fun setActiveDrawingInfoById(id: String?) {
        if (id == null) {
            activeDrawingInfo.postValue(null)
            setActiveDrawingInfoTitle("Untitled")
            return
        }
        val onSuccess = { drawing: UserDrawing ->
            activeDrawingInfo.postValue(drawing)
            setActiveDrawingInfoTitle(drawing.title)
        }

        val onError = {
            activeDrawingInfo.postValue(null)
            setActiveDrawingInfoTitle("Untitled")
        }
        getDrawingByDrawingId(id, onSuccess, onError)
    }

    fun deleteDrawing(drawingId: String) {
        // after deleting, update the current user's drawing history
        val onSuccess = {
            getCurrentUserDrawingHistory()
        }
        deleteDrawingByDrawingId(drawingId, onSuccess)
    }
}