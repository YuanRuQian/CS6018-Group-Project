package com.cs6018.canvasexample.utils

import android.app.Application
import com.cs6018.canvasexample.data.DrawingInfoDatabase
import com.cs6018.canvasexample.data.DrawingInfoRepository
import com.cs6018.canvasexample.network.ApiRepository
import com.cs6018.canvasexample.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    private val scope = CoroutineScope(SupervisorJob())
    private val db by lazy { DrawingInfoDatabase.getDatabase(applicationContext) }
    val drawingInfoRepository by lazy { DrawingInfoRepository(scope, db.drawingInfoDao()) }

    private val apiService = ApiService()
    val apiRepository by lazy { ApiRepository(apiService) }
}