package com.cs6018.canvasexample.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ShakeDetectionViewModel : ViewModel() {
    // Enum to represent the shake states
    enum class ShakeState {
        LIGHT_SHAKE,
        HARD_SHAKE,
        NO_SHAKE
    }

    // Current shake state
    private var _shakeState = mutableStateOf(ShakeState.NO_SHAKE)

    // Expose isLightShake as Composable state
    val isLightShake: MutableState<Boolean> = mutableStateOf(false)

    // Expose isHardShake as Composable state
    val isHardShake: MutableState<Boolean> = mutableStateOf(false)

    // Function to set the shake state
    fun setAsLightShake() {
        if (_shakeState.value != ShakeState.NO_SHAKE) {
            return
        }
        Log.d("HearShake", "hearLightShake: ")
        _shakeState.value = ShakeState.LIGHT_SHAKE
        isLightShake.value = true
    }

    // Function to set the shake state
    fun setAsHardShake() {
        if (_shakeState.value != ShakeState.NO_SHAKE) {
            return
        }
        Log.d("HearShake", "hearHardShake: ")
        _shakeState.value = ShakeState.HARD_SHAKE
        isHardShake.value = true
    }

    // Function to set the shake state
    fun setAsNoShake() {
        _shakeState.value = ShakeState.NO_SHAKE
        isLightShake.value = false
        isHardShake.value = false
    }
}
