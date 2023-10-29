package com.cs6018.canvasexample.data


import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.utils.bitmapToByteArray
import com.cs6018.canvasexample.utils.deleteImageFile
import com.cs6018.canvasexample.utils.overwriteCurrentImageFile
import com.cs6018.canvasexample.utils.saveImage
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

    private fun updateDrawingInfoTitle(title:String) {
        repository.updateDrawingInfoTitle(title, activeDrawingInfo.value?.id ?: 0)
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).value = imageBitmap
        Log.d("DrawingInfoViewModel", "Bitmap is set as activeCapturedImage.")
    }

    fun addDrawingInfoWithRecentCapturedImage(context: Context, drawingTitle: String): String? {

        val bitmap = activeCapturedImage.value
        if (bitmap == null) {
            Log.d("DrawingInfoViewModel", "Bitmap is null.")
            return null
        }

        if (activeDrawingInfo.value == null) {

            val imagePath = saveImage(bitmap, context)
            if (imagePath == null) {
                Log.d("DrawingInfoViewModel", "Image path is null.")
                return null
            }

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)

            addDrawingInfo(drawingTitle, imagePath, bitmapToByteArray(thumbnail))
            return imagePath
        } else {
            // TODO: update the current drawing's title
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
            if (imagePath == null) {
                Log.d("DrawingInfoViewModel", "Image path is null.")
                return null
            }
            if (drawingTitle != activeDrawingInfo.value?.drawingTitle) {
                Log.d("DrawingInfoViewModel",  "$drawingTitle, ${activeDrawingInfo.value?.drawingTitle}")
                updateDrawingInfoTitle(drawingTitle)
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