package com.cs6018.canvasexample


import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date


class DrawingInfoViewModel(private val repository: DrawingInfoRepository) : ViewModel() {

    var activeDrawingInfo: LiveData<DrawingInfo?> = repository.activeDrawingInfo

    private val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)

    val allDrawingInfo: LiveData<List<DrawingInfo>> = repository.allDrawingInfo

    fun getActiveCapturedImage(): LiveData<Bitmap?> {
        return activeCapturedImage
    }

    suspend fun setActiveDrawingInfoById(id: Int?) {
        repository.setActiveDrawingInfoById(id ?: 0)
    }

    fun addDrawingInfo(title: String, imageUrl: String?, thumbnail: ByteArray?) {
        val drawingInfo = DrawingInfo(Date(), Date(), title, imageUrl, thumbnail)
        repository.addNewDrawingInfo(drawingInfo)
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).postValue(imageBitmap)
        Log.d("DrawingInfoViewModel", "Bitmap is set as activeCapturedImage.")
    }

    fun addDrawingInfoWithRecentCapturedImage(context: Context): String? {

        val bitmap = activeCapturedImage.value
        if (bitmap == null) {
            Log.d("DrawingInfoViewModel", "Bitmap is null.")
            return null
        }

        if (activeDrawingInfo.value == null) {
            val title = "Untitled"


            val imagePath = saveImage(bitmap, context)
            if (imagePath == null) {
                Log.d("DrawingInfoViewModel", "Image path is null.")
                return null
            }

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            addDrawingInfo(title, imagePath, bitmapToByteArray(thumbnail))
            return imagePath
        } else {
            // TODO: update the current drawing's title
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
            if (imagePath == null) {
                Log.d("DrawingInfoViewModel", "Image path is null.")
                return null
            }

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            updateThumbnailForActiveDrawingInfo(bitmapToByteArray(thumbnail))

            return imagePath
        }
    }

    fun updateThumbnailForActiveDrawingInfo(thumbnail: ByteArray) {
        repository.updateDrawingInfoThumbnail(thumbnail, activeDrawingInfo.value?.id ?: 0)
    }

    suspend fun deleteDrawingInfoWithId(drawingInfo: DrawingInfo, context: Context) {
        deleteImageFile(drawingInfo.imagePath, context)
        repository.deleteDrawingInfoWithId(drawingInfo.id)
    }
}