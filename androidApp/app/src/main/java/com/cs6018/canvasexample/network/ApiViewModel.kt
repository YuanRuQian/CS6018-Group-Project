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

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    val currentUserDrawingHistory: LiveData<List<UserDrawing>> =
        repository.currentUserDrawingHistory

    val currentUserExploreFeed: LiveData<List<UserDrawing>> =
        repository.currentUserExploreFeed

    var activeDrawingInfo: LiveData<UserDrawing?> = repository.activeDrawingInfo

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

    private fun updateDrawingTitleById(thumbnail: String) {
        repository.updateDrawingTitleById(activeDrawingTitle.value ?: "Untitled", thumbnail)
    }

    private fun postNewDrawing(imagePath: String, thumbnail: String) {
        repository.postNewDrawing(imagePath, thumbnail)
    }

    fun addDrawingInfoWithRecentCapturedImage(context: Context): String? {
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

    fun getCurrentUserDrawingHistory() {
        repository.getCurrentUserDrawingHistory()
    }

    fun getCurrentUserExploreFeed() {
        repository.getCurrentUserExploreFeed()
    }

    fun setActiveDrawingInfoById(drawingId: String?) {
        repository.setActiveDrawingInfoById(drawingId)
    }

    fun deleteDrawing(drawingId: String) {
        repository.deleteDrawing(drawingId)
    }
}