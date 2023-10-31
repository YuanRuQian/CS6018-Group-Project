package com.cs6018.canvasexample.utils

import android.app.Application
import com.cs6018.canvasexample.network.ApiRepository

class DrawingApplication : Application() {
    val apiRepository by lazy { ApiRepository() }
}