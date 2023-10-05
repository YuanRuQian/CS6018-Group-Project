package com.cs6018.canvasexample

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PathPropertiesInstrumentedTest {
    @Test
    fun testInitialization() {
        val pathProperties = PathProperties()
        Assert.assertEquals(10f, pathProperties.strokeWidth)
        Assert.assertEquals(Color.Black, pathProperties.color)
        Assert.assertEquals(StrokeCap.Round, pathProperties.strokeCap)
        Assert.assertEquals(StrokeJoin.Round, pathProperties.strokeJoin)
        Assert.assertFalse(pathProperties.eraseMode)
    }
}