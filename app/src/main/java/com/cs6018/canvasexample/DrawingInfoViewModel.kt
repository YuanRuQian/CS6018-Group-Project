package com.cs6018.canvasexample



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Date

class DrawingInfoViewModel : ViewModel() {
    // State holding a list of DrawingInfo objects
    private var _drawingInfoList by mutableStateOf<List<DrawingInfo>>(emptyList())

    // Expose the list as a read-only property
    val drawingInfoList: List<DrawingInfo>
        get() = _drawingInfoList

    // TODO: replace this with your data source
    init {
        val randomData = generateRandomDrawingInfoList(10)
        _drawingInfoList = randomData
    }

    // Helper function to generate random DrawingInfo data (replace with your data source)
    private fun generateRandomDrawingInfoList(count: Int): List<DrawingInfo> {
        val dataList = mutableListOf<DrawingInfo>()
        val random = java.util.Random(System.currentTimeMillis())
        val currentDate = Date()

        for (index in 0 until count) {
            dataList.add(
                DrawingInfo(
                    id = index,
                    lastModifiedDate = Date(currentDate.time - random.nextInt(30) * 24 * 60 * 60 * 1000L),
                    createdDate = Date(currentDate.time - random.nextInt(365) * 24 * 60 * 60 * 1000L),
                    drawingTitle = "Drawing $index"
                )
            )
        }

        return dataList
    }

    fun addDrawingInfo(drawingInfo: DrawingInfo) {
        _drawingInfoList = _drawingInfoList + drawingInfo
    }

    fun removeDrawingInfo(drawingInfo: DrawingInfo) {
        _drawingInfoList = _drawingInfoList - drawingInfo
    }

    fun updateDrawingInfoLastModifiedDate(id: Int, lastModifiedDate: Date) {
        val index = _drawingInfoList.indexOfFirst { it.id == id }
        if (index != -1) {
            val drawingInfo = _drawingInfoList[index]
            _drawingInfoList = _drawingInfoList.toMutableList().apply {
                set(index, drawingInfo.copy(lastModifiedDate = lastModifiedDate))
            }
        }
    }
}
