package com.cs6018.canvasexample

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// TODO: Allow user to change pen shape
// TODO: Confine the playground as a square
@Composable
fun Playground( navController: NavHostController, viewModel: PathPropertiesViewModel) {

    LocalContext.current

    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    val paths = viewModel.paths

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    val pathsUndone = viewModel.pathsUndone

    /**
     * Canvas touch state. [MotionEvent.Idle] by default, [MotionEvent.Down] at first contact,
     * [MotionEvent.Move] while dragging and [MotionEvent.Up] when first pointer is up
     */
    val motionEvent = viewModel.motionEvent

    /**
     * Current position of the pointer that is pressed or being moved
     */
    val currentPosition = viewModel.currentPosition

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition = viewModel.previousPosition

    /**
     * Draw mode, erase mode or touch mode to
     */
    val drawMode = viewModel.drawMode

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    val currentPath = viewModel.currentPath

    /**
     * Properties of path that is currently being drawn between
     * [MotionEvent.Down] and [MotionEvent.Up].
     */
    val currentPathProperty = viewModel.currentPathProperty

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        val drawModifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
            .background(Color.White)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Down)
                    viewModel.updateCurrentPosition(pointerInputChange.position)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) pointerInputChange.consume()
                },
                onDrag = { pointerInputChange ->

                    viewModel.updateMotionEvent(MotionEvent.Move)
                    viewModel.updateCurrentPosition(pointerInputChange.position)

                    if (drawMode.value == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        Log.d("DRAG", "DRAG: $change")
                        paths.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }
                        currentPath.value.translate(change)
                    }
                    if (pointerInputChange.positionChange() != Offset.Zero) pointerInputChange.consume()

                },
                onDragEnd = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Up)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) pointerInputChange.consume()
                }
            )

        Canvas(modifier = drawModifier) {
            when (motionEvent.value) {
                MotionEvent.Down -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.moveTo(currentPosition.value.x, currentPosition.value.y)
                    }

                    previousPosition = currentPosition

                }

                MotionEvent.Move -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.quadraticBezierTo(
                            previousPosition.value.x,
                            previousPosition.value.y,
                            (previousPosition.value.x + currentPosition.value.x) / 2,
                            (previousPosition.value.y + currentPosition.value.y) / 2
                        )
                    }

                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.lineTo(currentPosition.value.x, currentPosition.value.y)

                        // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                        paths.add(Pair(currentPath.value, currentPathProperty.value))

                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle

                        viewModel.updateCurrentPath(Path())



                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        // Should update the stroke width & color here!
                        viewModel.updateCurrentPathProperty(PathProperties(
                            strokeWidth =  currentPathProperty.value.strokeWidth,
                            color = currentPathProperty.value.color,
                            strokeCap = currentPathProperty.value.strokeCap,
                            strokeJoin = currentPathProperty.value.strokeJoin,
                            eraseMode = currentPathProperty.value.eraseMode
                        ))
                    }

                    // Since new path is drawn no need to store paths to undone
                    pathsUndone.clear()

                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    viewModel.updatePreviousPosition(currentPosition.value)
                    viewModel.updateCurrentPosition(Offset.Unspecified)
                    viewModel.updateMotionEvent(MotionEvent.Idle)
                }

                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.value.strokeWidth,
                                cap = currentPathProperty.value.strokeCap,
                                join = currentPathProperty.value.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                if (motionEvent.value != MotionEvent.Idle) {
                    if (!currentPathProperty.value.eraseMode) {
                        drawPath(
                            color = currentPathProperty.value.color,
                            path = currentPath.value,
                            style = Stroke(
                                width = currentPathProperty.value.strokeWidth,
                                cap = currentPathProperty.value.strokeCap,
                                join = currentPathProperty.value.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath.value,
                            style = Stroke(
                                width = currentPathProperty.value.strokeWidth,
                                cap = currentPathProperty.value.strokeCap,
                                join = currentPathProperty.value.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }

        // TODO: share the same bottom app bar across all screens
        BottomAppBar(
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("penCustomizer") // Navigate to penCustomizer
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Pen Customizer"
                        )
                    }
                }
            }
        )
    }
}
