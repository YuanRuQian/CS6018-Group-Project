package com.cs6018.canvasexample.network

class ApiRepository(private val apiService: ApiService) {
    suspend fun getAllDrawings(): List<DrawingResponse> {
        return apiService.getAllDrawings()
    }
}