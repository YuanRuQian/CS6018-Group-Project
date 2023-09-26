package com.cs6018.canvasexample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CapturableImageViewModel : ViewModel() {

    // Define MutableLiveData to represent the capturable image state
    private val _capturableImageState = MutableLiveData(CapturableImageState.DONE)

    // Expose LiveData to observe the capturable image state from the UI
    val capturableImageState: LiveData<CapturableImageState>
        get() = _capturableImageState

    fun markAsInProcess() {
        _capturableImageState.value = CapturableImageState.IN_PROCESS
        Log.d("CapturableImageViewModel", "Capturable image is marked as in process.")
    }

    fun markAsDone() {
        _capturableImageState.value = CapturableImageState.DONE
        Log.d("CapturableImageViewModel", "Capturable image is marked as done.")
    }
}

// Enum class representing the two states of the capturable image
enum class CapturableImageState {
    IN_PROCESS,
    DONE
}
