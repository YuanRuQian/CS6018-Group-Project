package com.cs6018.canvasexample

import java.util.Date

fun generateTestDrawingInfoList(): List<DrawingInfo> {
    val drawingInfoList = mutableListOf<DrawingInfo>()
    for (i in 0..10) {
        drawingInfoList.add(DrawingInfo(Date(), Date(), "Drawing $i", null, null))
    }
    return drawingInfoList
}