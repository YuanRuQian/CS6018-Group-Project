package com.cs6018.canvasexample

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class DrawingInfoRepository(private val scope: CoroutineScope, private val dao: DrawingInfoDAO) {

    val allDrawingInfo = dao.allDrawingInfo().asLiveData()
    fun addNewDrawingInfo(title: String, imageUrl: String?, thumbnail: ByteArray?) {
        scope.launch {
            dao.addDrawingInfo(
                DrawingInfo(Date(), Date(), title, imageUrl, thumbnail)
            )
        }
    }
}