package com.cs6018.canvasexample

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    val scope = CoroutineScope(SupervisorJob())
    val db by lazy { DrawingInfoDatabase.getDatabase(applicationContext) }
    val drawingInfoRepository by lazy { DrawingInfoRepository(scope, db.drawingInfoDao()) }
}