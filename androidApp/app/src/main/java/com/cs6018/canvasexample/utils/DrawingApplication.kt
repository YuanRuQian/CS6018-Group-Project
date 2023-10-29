package com.cs6018.canvasexample.utils

import android.app.Application
import com.cs6018.canvasexample.network.ApiRepository
import com.cs6018.canvasexample.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    private val scope = CoroutineScope(SupervisorJob())
    private val apiService = ApiService()
    val apiRepository by lazy { ApiRepository(scope, apiService) }
}