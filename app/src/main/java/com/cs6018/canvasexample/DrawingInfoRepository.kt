package com.cs6018.canvasexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class DrawingInfoRepository(private val scope: CoroutineScope, private val dao: DrawingInfoDAO) {

    private var activeDrawingInfoId: LiveData<Int?> = MutableLiveData(null)

    // Function to set the active drawing info ID
    fun setActiveDrawingInfoId(id: Int?) {
        activeDrawingInfoId = MutableLiveData(id)
    }

    val activeDrawingInfo = dao.activeDrawingInfo(activeDrawingInfoId.value ?: 0).asLiveData()

    val allDrawingInfo = dao.allDrawingInfo().asLiveData()
    fun addNewDrawingInfo(drawingInfo: DrawingInfo) {
        scope.launch {
            dao.addDrawingInfo(drawingInfo)
        }
    }

    fun updateDrawingInfoThumbnail(bitmapToByteArray: ByteArray, id: Int) {
        scope.launch {
            dao.updateDrawingInfoThumbnailAndLastModifiedTimeWithId(bitmapToByteArray, Date() ,id)
        }
    }
}