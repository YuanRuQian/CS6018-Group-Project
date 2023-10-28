package com.cs6018.canvasexample.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _drawings = MutableStateFlow<List<DrawingResponse>>(emptyList())
    val drawings: StateFlow<List<DrawingResponse>> = _drawings

    init {
        getAllDrawings()
    }

    private fun getAllDrawings() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedDrawings = repository.getAllDrawings()
            _drawings.emit(fetchedDrawings)
        }
    }
}