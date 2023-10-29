package com.cs6018.canvasexample.network

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _currentUserDrawingHistory = MutableStateFlow<List<DrawingResponse>>(emptyList())
    val currentUserDrawingHistory: StateFlow<List<DrawingResponse>> = _currentUserDrawingHistory

    var activeDrawingInfo: LiveData<DrawingResponse?> = MutableLiveData(null)

    private val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)

    fun getCurrentUserDrawingHistory(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedDrawings = repository.getCurrentUserDrawingHistory(userId)
            _currentUserDrawingHistory.emit(fetchedDrawings)
        }
    }
}