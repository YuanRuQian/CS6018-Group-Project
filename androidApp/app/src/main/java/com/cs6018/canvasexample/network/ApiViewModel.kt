package com.cs6018.canvasexample.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.utils.bitmapToBase64String

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    val currentUserDrawingHistory: LiveData<List<UserDrawing>> =
        repository.currentUserDrawingHistory

    val currentUserExploreFeed: LiveData<List<UserDrawing>> =
        repository.currentUserExploreFeed

    var activeDrawingInfo: LiveData<UserDrawing?> = repository.activeDrawingInfo

    var activeDrawingBackgroundImageReference: LiveData<String?> =
        repository.activeDrawingBackgroundImageReference

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

    private fun updateDrawingByDrawingId(title: String, thumbnail: String) {
        repository.updateDrawingByDrawingId(title, thumbnail)
    }

    private fun postNewDrawing(title: String, imagePath: String, thumbnail: String) {
        repository.postNewDrawing(title, imagePath, thumbnail)
    }

    fun addDrawingInfoWithRecentCapturedImage(context: Context) {
        val bitmap = activeCapturedImage.value
        if (bitmap == null) {
            Log.d("ApiViewModel", "Bitmap is null.")
            return
        }

        if (activeDrawingInfo.value == null) {

            val onSuccess = { imagePath: String, bm: Bitmap ->
                Toast.makeText(context, "Drawing was saved successfully", Toast.LENGTH_LONG).show()
                val thumbnail = bitmapToBase64String(bm)
                val title = activeDrawingTitle.value ?: "Untitled"
                postNewDrawing(title, imagePath, thumbnail)
                setActiveDrawingInfoById(null)
                setActiveCapturedImage(null)
            }

            val onError = {
                Toast.makeText(context, "Error occurred while saving drawing", Toast.LENGTH_LONG)
                    .show()
                setActiveDrawingInfoById(null)
                setActiveCapturedImage(null)
            }

            uploadImageToCloudStorage(bitmap, onSuccess, onError)
        } else {

            val onSuccess = { bm: Bitmap ->
                Toast.makeText(context, "Drawing was saved successfully", Toast.LENGTH_LONG).show()
                val thumbnail = bitmapToBase64String(bm)
                val title = activeDrawingTitle.value ?: "Untitled"
                updateDrawingByDrawingId(title, thumbnail)
                setActiveDrawingInfoById(null)
                setActiveCapturedImage(null)
            }

            val onError = {
                Toast.makeText(context, "Error occurred while saving drawing", Toast.LENGTH_LONG)
                    .show()
                setActiveDrawingInfoById(null)
                setActiveCapturedImage(null)
            }

            val previousImagePath = activeDrawingInfo.value?.imagePath ?: ""

            overwriteImageToCloudStorage(bitmap, previousImagePath, onSuccess, onError)
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

    fun setActiveDrawingBackgroundImageReference(imageReference: String?) {
        repository.setActiveDrawingBackgroundImageReference(imageReference)
    }

    fun resetData() {
        repository.resetData()
    }
}