package com.cs6018.canvasexample.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.utils.bitmapToBase64String
import com.cs6018.canvasexample.utils.overwriteCurrentImageFile
import com.cs6018.canvasexample.utils.saveImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    val currentUserDrawingHistory: LiveData<List<DrawingResponse>> =
        repository.currentUserDrawingHistory

    val currentUserExploreFeed: LiveData<List<DrawingResponse>> =
        repository.currentUserExploreFeed

    var activeDrawingInfo: LiveData<DrawingResponse?> = repository.activeDrawingInfo

    var activeDrawingTitle: LiveData<String?> = repository.activeDrawingTitle

    private val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)

    fun setActiveDrawingInfoTitle(title: String) {
        repository.setActiveDrawingInfoTitle(title)
        Log.d("ApiViewModel", "setActiveDrawingInfoTitle: ${activeDrawingInfo.value}")
    }

    fun getActiveCapturedImage(): LiveData<Bitmap?> {
        return activeCapturedImage
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).value = imageBitmap
        Log.d("ApiViewModel", "Bitmap is set as activeCapturedImage.")
    }

    private suspend fun updateDrawingTitleById(thumbnail: String) {
        repository.updateDrawingTitleById(activeDrawingTitle.value ?: "Untitled", thumbnail)
    }

    private suspend fun postNewDrawing(creatorId: String, imagePath: String, thumbnail: String) {
        repository.postNewDrawing(creatorId, imagePath, thumbnail)
    }

    suspend fun addDrawingInfoWithRecentCapturedImage(context: Context): String? {
        val bitmap = activeCapturedImage.value
        if (bitmap == null) {
            Log.d("ApiViewModel", "Bitmap is null.")
            return null
        }

        if (activeDrawingInfo.value == null) {
            val imagePath = saveImage(bitmap, context)
            if (imagePath == null) {
                Log.d("ApiViewModel", "Image path is null.")
                return null
            }

            val thumbnail = bitmapToBase64String(
                bitmap
            )

            postNewDrawing(
                Firebase.auth.currentUser?.uid ?: "",
                imagePath,
                thumbnail
            )
            return imagePath
        } else {
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
            if (imagePath == null) {
                Log.d("ApiViewModel", "Image path is null.")
                return null
            }

            val thumbnail = bitmapToBase64String(bitmap)
            updateDrawingTitleById(thumbnail)
            return imagePath
        }
    }


    fun getCurrentUserDrawingHistory(userId: String) {
        repository.getCurrentUserDrawingHistory(userId)
    }

    suspend fun getCurrentUserExploreFeed(userId: String) {
        repository.getCurrentUserExploreFeed(userId)
    }

    fun setActiveDrawingInfoById(id: Int?) {
        repository.setActiveDrawingInfoById(id ?: 0)
    }

    fun deleteDrawingById(id: Int) {
        repository.deleteDrawingById(id)
    }
}