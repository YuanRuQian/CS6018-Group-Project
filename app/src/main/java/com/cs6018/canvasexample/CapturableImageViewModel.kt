package com.cs6018.canvasexample

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

class CapturableImageViewModel : ViewModel() {

    private val _signalChannel = MutableLiveData<Channel<Unit>>(Channel())

    val signalChannel: MutableLiveData<Channel<Unit>>
        get() = _signalChannel


    fun setNewSignalChannel(channel: Channel<Unit> = Channel()) {
        Log.d("CanvasPage", "CapturableImageViewModel.setNewSignalChannel")
        _signalChannel.value = channel
    }

    fun fireSignal() {
        Log.d("CanvasPage", "CapturableImageViewModel.fireSignal")
        _signalChannel.value?.trySend(Unit)?.isSuccess
    }
}
