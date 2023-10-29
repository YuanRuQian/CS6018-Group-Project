package com.cs6018.canvasexample.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.cs6018.canvasexample.data.DrawingInfo

class ApiRepository(private val apiService: ApiService) {

    suspend fun getAllDrawings(): List<DrawingResponse> {
        return apiService.getAllDrawings()
    }

    suspend fun getCurrentUserDrawingHistory(userId: String): List<DrawingResponse> {
        return apiService.getCurrentUserDrawingHistory(userId)
    }
}