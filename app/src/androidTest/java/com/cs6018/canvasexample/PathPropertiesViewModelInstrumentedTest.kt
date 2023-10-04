package com.cs6018.canvasexample

import android.util.Log
import androidx.compose.material3.AssistChip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PathPropertiesViewModelInstrumentedTest {

    @Test
    fun testHexColorCodeWithCoroutin() {
        val viewModel = PathPropertiesViewModel()
        var callbackFired = false
        runBlocking {
            val before = viewModel.hexColorCode.value
            // Launch a coroutine to collect changes in the StateFlow
            launch {
                viewModel.hexColorCode.collect { newValue ->
                    callbackFired = true
                    Log.d("New-value: ", newValue)
                }
            }
            viewModel.updateHexColorCode("#FF0000")
            Assert.assertNotSame(before, viewModel.motionEvent.value)
            Assert.assertTrue(callbackFired) // TODO failed
        }
    }

    @Test
    fun testInitialization() {
        val vm = PathPropertiesViewModel()
        Assert.assertSame("#ffffff", vm.hexColorCode.value)
        // currentPathProperties is test in PathPropertiesInstrumentedTest
        // TODO test eraseDrawToggleButtonIcon?
        Assert.assertEquals(PathPropertiesViewModel.EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT, vm.eraseDrawToggleButtonText.value)
        Assert.assertEquals(MotionEvent.Idle, vm.motionEvent.value);
        Assert.assertEquals(Offset.Unspecified, vm.currentPosition.value)
        Assert.assertEquals(Offset.Unspecified, vm.previousPosition.value)
    }

    // TODO test update paths an paths undone

    @Test
    fun testReset() {
        val vm = PathPropertiesViewModel()
        vm.updateHexColorCode("#FF0000")
        vm.updateMotionEvent(MotionEvent.Up)
        vm.reset()
        Assert.assertSame("#ffffff", vm.hexColorCode.value)
    }


    @Test
    fun testUpdateHexColorCode() {
        val vm = PathPropertiesViewModel()
        val before = vm.hexColorCode.value
        vm.updateHexColorCode("#FF0000")
        Assert.assertNotSame(before, vm.hexColorCode.value)
    }

    @Test
    fun testUpdateMotionEvent() {
        val vm = PathPropertiesViewModel()
        val before = vm.motionEvent.value
        vm.updateMotionEvent(MotionEvent.Down)
        Assert.assertNotSame(before, vm.motionEvent.value)
    }


    @Test
    fun testUpdateCurrentPathProperty() {
        val vm = PathPropertiesViewModel()
        val oldProperty = vm.currentPathProperty.value
        vm.updateCurrentPathProperty(Color.Blue, 10f, StrokeCap.Butt, StrokeJoin.Miter)
        val newProperty = vm.currentPathProperty.value
        Assert.assertNotSame(oldProperty.color, newProperty.color)
        Assert.assertNotSame(oldProperty.strokeWidth, newProperty.strokeWidth)
        Assert.assertNotSame(oldProperty.strokeCap, newProperty.strokeCap)
        Assert.assertNotSame(oldProperty.strokeJoin, newProperty.strokeJoin)
    }

    @Test
    fun testIsEraseMode() {
        val vm = PathPropertiesViewModel()
        Assert.assertFalse(vm.isEraseMode())
        vm.currentPathProperty.value.eraseMode = true
        Assert.assertTrue(vm.isEraseMode())
    }

    // TODO test toggleDrawMode?

    @Test
    fun testUndoLastAction() {
        val vm = PathPropertiesViewModel()
        Assert.assertEquals(0, vm.paths.size)
        vm.paths.add(Pair(Path(), PathProperties()))
        Assert.assertEquals(1, vm.paths.size)
        vm.paths.add(Pair(Path(), PathProperties()))
        Assert.assertEquals(2, vm.paths.size)
        vm.undoLastAction()
        Assert.assertEquals(1, vm.paths.size)
    }
}