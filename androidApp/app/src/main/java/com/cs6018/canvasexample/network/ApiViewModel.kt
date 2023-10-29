package com.cs6018.canvasexample.network

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.utils.overwriteCurrentImageFile
import com.cs6018.canvasexample.utils.saveImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    val currentUserDrawingHistory: LiveData<List<DrawingResponse>> =
        repository.currentUserDrawingHistory

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

    private suspend fun updateDrawingTitleById() {
        repository.updateDrawingTitleById(activeDrawingTitle.value ?: "Untitled")
    }

    private suspend fun postNewDrawing(creatorId: String, imagePath: String) {
        repository.postNewDrawing(creatorId, imagePath)
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

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)

            // TODO: add new thumbnail here
            postNewDrawing(
                Firebase.auth.currentUser?.uid ?: "",
                imagePath
            )
            return imagePath
        } else {
            // TODO: update the current drawing's title
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
            if (imagePath == null) {
                Log.d("ApiViewModel", "Image path is null.")
                return null
            }
            updateDrawingTitleById()

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            // TODO: update the active drawing's thumbnail here
            // updateThumbnailForActiveDrawingInfo(bitmapToByteArray(thumbnail))
            return imagePath
        }
    }


    fun getCurrentUserDrawingHistory(userId: String) {
        repository.getCurrentUserDrawingHistory(userId)
    }

    fun setActiveDrawingInfoById(id: Int?) {
        repository.setActiveDrawingInfoById(id ?: 0)
    }

    fun deleteDrawingById(id: Int) {
        repository.deleteDrawingById(id)
    }
}