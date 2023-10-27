package com.cs6018.canvasexample.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class DrawingInfoRepository(private val scope: CoroutineScope, private val dao: DrawingInfoDAO) {

    var activeDrawingInfo: MutableLiveData<DrawingInfo?> = MutableLiveData(null)

    val allDrawingInfo = dao.allDrawingInfo().asLiveData()

    fun addNewDrawingInfo(drawingInfo: DrawingInfo) {
        scope.launch {
            dao.addDrawingInfo(drawingInfo)
        }
    }

    fun updateDrawingInfoTitle(title: String, id: Int) {
        scope.launch {
            dao.updateDrawingInfoTitle(title, id)
        }
    }

    fun updateDrawingInfoThumbnail(bitmapToByteArray: ByteArray, id: Int) {
        scope.launch {
            dao.updateDrawingInfoThumbnailAndLastModifiedTimeWithId(bitmapToByteArray, Date() ,id)
        }
    }


    suspend fun setActiveDrawingInfoById(i: Int) {
        withContext(Dispatchers.IO) {
            Log.d("DrawingInfoRepository", "setActiveDrawingInfoById($i)")
            val newActiveDrawingInfo = dao.fetchDrawingInfoWithId(i).firstOrNull()
            activeDrawingInfo.postValue(newActiveDrawingInfo)
            Log.d("DrawingInfoRepository", "activeDrawingInfo is set to ${activeDrawingInfo.value?.imagePath}")
        }
    }

    suspend fun deleteDrawingInfoWithId(id: Int) {
        withContext(Dispatchers.IO) {
            Log.d("DrawingInfoRepository", "deleteDrawingInfoWithId($id)")
            dao.deleteDrawingInfoWithId(id)
        }
    }
}