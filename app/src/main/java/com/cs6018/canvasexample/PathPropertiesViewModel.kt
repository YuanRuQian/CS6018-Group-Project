package com.cs6018.canvasexample

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PathPropertiesViewModel : ViewModel() {
    enum class EraseDrawToggleButtonIconEnum(val iconResource: Int) {
        ERASE_MODE_ICON(R.drawable.ink_eraser_off),
        DRAW_MODE_ICON(R.drawable.ink_eraser)
    }

    enum class EraseDrawToggleButtonTextEnum(val text: String) {
        ERASE_MODE_TEXT("Draw"),
        DRAW_MODE_TEXT("Erase")
    }

    private val _hexColorCode = MutableStateFlow("#ffffff")
    val hexColorCode: StateFlow<String> = _hexColorCode.asStateFlow()

    private val _currentPathProperty = MutableStateFlow(PathProperties())
    val currentPathProperty: StateFlow<PathProperties> = _currentPathProperty.asStateFlow()

    private val _eraseDrawToggleButtonIcon =
        MutableStateFlow(EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON)
    val eraseDrawToggleButtonIcon: StateFlow<EraseDrawToggleButtonIconEnum> =
        _eraseDrawToggleButtonIcon.asStateFlow()

    private val _eraseDrawToggleButtonText =
        MutableStateFlow(EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT)
    val eraseDrawToggleButtonText: StateFlow<EraseDrawToggleButtonTextEnum> =
        _eraseDrawToggleButtonText.asStateFlow()

    // Paths that are added
    val paths = mutableStateListOf<Pair<Path, PathProperties>>()

    // Paths that are undone via button
    val pathsUndone = mutableStateListOf<Pair<Path, PathProperties>>()

    // Canvas touch state
    val motionEvent = mutableStateOf(MotionEvent.Idle)

    // Current position of the pointer that is pressed or being moved
    val currentPosition = mutableStateOf(Offset.Unspecified)

    // Previous motion event before next touch is saved into this current position.
    val previousPosition = mutableStateOf(Offset.Unspecified)

    // Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]
    val currentPath = mutableStateOf(Path())

    fun reset() {
        _hexColorCode.value = "#ffffff"
        _currentPathProperty.value = PathProperties()
        _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON
        _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT
        paths.clear()
        pathsUndone.clear()
        motionEvent.value = MotionEvent.Idle
        currentPosition.value = Offset.Unspecified
        previousPosition.value = Offset.Unspecified
        currentPath.value = Path()
    }

    fun updateHexColorCode(newHexColorCode: String) {
        _hexColorCode.value = newHexColorCode
    }

    fun updateMotionEvent(newMotionEvent: MotionEvent) {
        motionEvent.value = newMotionEvent
    }

    fun updateCurrentPosition(newPosition: Offset) {
        currentPosition.value = newPosition
    }

    fun updatePreviousPosition(newPosition: Offset) {
        previousPosition.value = newPosition
    }

    fun updateCurrentPath(newPath: Path) {
        currentPath.value = newPath
    }

    fun updateCurrentPathProperty(newProperty: PathProperties) {
        _currentPathProperty.value = newProperty
    }

    fun updateCurrentPathProperty(
        newColor: Color? = null,
        newStrokeWidth: Float? = null,
        newStrokeCap: StrokeCap? = null,
        newStrokeJoin: StrokeJoin? = null
    ) {
        val newProperty = currentPathProperty.value.copy()
        newColor?.let { newProperty.color = it }
        newStrokeWidth?.let { newProperty.strokeWidth = it }
        newStrokeCap?.let { newProperty.strokeCap = it }
        newStrokeJoin?.let { newProperty.strokeJoin = it }
        _currentPathProperty.value = newProperty
    }

    fun isEraseMode(): Boolean {
        return currentPathProperty.value.eraseMode
    }

    fun toggleDrawMode() {
        currentPathProperty.value.eraseMode = !currentPathProperty.value.eraseMode
        if (currentPathProperty.value.eraseMode) {
            _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.ERASE_MODE_ICON
            _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.ERASE_MODE_TEXT
        } else {
            _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON
            _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT
        }
    }

    fun undoLastAction() {
        if (paths.isNotEmpty()) {
            val lastItem = paths.last()
            val lastPath = lastItem.first
            val lastPathProperty = lastItem.second
            paths.remove(lastItem)
            pathsUndone.add(Pair(lastPath, lastPathProperty))
        }
    }
}
