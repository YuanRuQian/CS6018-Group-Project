package com.cs6018.canvasexample


import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date


class DrawingInfoViewModel(private val repository: DrawingInfoRepository) : ViewModel() {

    val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)

    val allDrawingInfo: LiveData<List<DrawingInfo>> = repository.allDrawingInfo

    val activeDrawingInfo: LiveData<DrawingInfo?> = repository.activeDrawingInfo

    fun setActiveDrawingInfoId(id: Int?) {
        repository.setActiveDrawingInfoId(id)
    }

    private fun addDrawingInfo(title: String, imageUrl: String?, thumbnail: ByteArray?) {
        val drawingInfo = DrawingInfo(Date(), Date(), title, imageUrl, thumbnail)
        repository.addNewDrawingInfo(drawingInfo)
    }

    fun getActiveDrawingInfoImagePath(): String? {
        return activeDrawingInfo.value?.imagePath
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).value = imageBitmap
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
            // TODO: save the thumbnail of the image
            addDrawingInfo(title, imagePath, bitmapToByteArray(thumbnail))
            return imagePath
        } else {
            // TODO: update the current drawing's title
            val imagePath =
                saveImageToFilePath(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
            if (imagePath == null) {
                Log.d("DrawingInfoViewModel", "Image path is null.")
                return null
            }

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            updateThumbnailForActiveDrawingInfo(bitmapToByteArray(thumbnail))

            return imagePath
        }
    }

    private fun updateThumbnailForActiveDrawingInfo(thumbnail: ByteArray) {
        repository.updateDrawingInfoThumbnail(thumbnail, activeDrawingInfo.value?.id ?: 0)
    }


}