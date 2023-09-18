package com.cs6018.canvasexample

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dev.shreyaspatil.capturable.controller.CaptureController

@Composable
fun Playground(
    viewModel: PathPropertiesViewModel,
    paddingValues: PaddingValues,
    captureController: CaptureController
) {
    val paths = viewModel.paths

    val pathsUndone = viewModel.pathsUndone

    val motionEvent = viewModel.motionEvent

    val currentPosition = viewModel.currentPosition

    var previousPosition = viewModel.previousPosition

    val currentPath = viewModel.currentPath

    val currentPathProperty = viewModel.currentPathProperty

    val backgroundImageUri = getUriOfLastFile(LocalContext.current)

    val basePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(backgroundImageUri)
            .size(coil.size.Size.ORIGINAL)
            .allowHardware(false)
            .build()
    )

    val baseImageLoadedState = basePainter.state

    var baseImageBitmap: ImageBitmap? = null

    if (
        baseImageLoadedState is AsyncImagePainter.State.Success
    ) {
        baseImageBitmap =
            baseImageLoadedState.result.drawable.toBitmap()
                .asImageBitmap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(paddingValues)
    ) {
        val drawModifier = Modifier
            .drawBehind {
                if (baseImageBitmap != null) {
                    drawImage(
                        image = baseImageBitmap,
                        topLeft = Offset.Zero,
                    )
                    Log.d("CanvasPage", "image width ${baseImageBitmap.width}, height ${baseImageBitmap.height}")
                    Log.d("CanvasPage", "draw behind $backgroundImageUri")
                } else {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset.Zero,
                        size = size
                    )
                    Log.d("CanvasPage", "draw white rectangle")
                }
            }
            .background(Color.Transparent)
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Down)
                    viewModel.updateCurrentPosition(pointerInputChange.position)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                },
                onDrag = { pointerInputChange ->

                    viewModel.updateMotionEvent(MotionEvent.Move)
                    viewModel.updateCurrentPosition(pointerInputChange.position)

                    if (pointerInputChange.positionChange() != Offset.Zero) {
                        pointerInputChange.consume()
                    }

                },
                onDragEnd = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Up)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                }
            )


        CapturableWrapper(
            content = {
                Canvas(modifier = drawModifier) {
                    Log.d("CanvasPage", "canvas width: ${size.width}, height: ${size.height}")
                    when (motionEvent.value) {
                        MotionEvent.Down -> {
                            currentPath.value.moveTo(
                                currentPosition.value.x,
                                currentPosition.value.y
                            )
                            previousPosition = currentPosition
                        }

                        MotionEvent.Move -> {

                            currentPath.value.quadraticBezierTo(
                                previousPosition.value.x,
                                previousPosition.value.y,
                                (previousPosition.value.x + currentPosition.value.x) / 2,
                                (previousPosition.value.y + currentPosition.value.y) / 2
                            )

                            previousPosition = currentPosition
                        }

                        MotionEvent.Up -> {
                            currentPath.value.lineTo(
                                currentPosition.value.x,
                                currentPosition.value.y
                            )

                            paths.add(Pair(currentPath.value, currentPathProperty.value))

                            viewModel.updateCurrentPath(Path())

                            viewModel.updateCurrentPathProperty(
                                PathProperties(
                                    strokeWidth = currentPathProperty.value.strokeWidth,
                                    color = currentPathProperty.value.color,
                                    strokeCap = currentPathProperty.value.strokeCap,
                                    strokeJoin = currentPathProperty.value.strokeJoin,
                                    eraseMode = currentPathProperty.value.eraseMode
                                )
                            )

                            pathsUndone.clear()

                            viewModel.updatePreviousPosition(currentPosition.value)
                            viewModel.updateCurrentPosition(Offset.Unspecified)
                            viewModel.updateMotionEvent(MotionEvent.Idle)
                        }

                        else -> Unit
                    }

                    // TODO: optimize drawing process ( not recreating all paths every time )
                    with(drawContext.canvas.nativeCanvas) {

                        val checkPoint = saveLayer(null, null)

                        paths.forEach {

                            val path = it.first
                            val property = it.second
                            val style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )

                            if (property.eraseMode) {
                                drawPath(
                                    color = Color.Transparent,
                                    path = path,
                                    style = style,
                                    blendMode = BlendMode.Clear
                                )
                            } else {
                                drawPath(
                                    color = property.color,
                                    path = path,
                                    style = style,
                                    alpha = property.color.alpha
                                )
                            }
                        }

                        if (motionEvent.value != MotionEvent.Idle) {
                            val style = Stroke(
                                width = currentPathProperty.value.strokeWidth,
                                cap = currentPathProperty.value.strokeCap,
                                join = currentPathProperty.value.strokeJoin
                            )
                            if (currentPathProperty.value.eraseMode) {
                                drawPath(
                                    color = Color.Transparent,
                                    path = currentPath.value,
                                    style = style,
                                    blendMode = BlendMode.Clear
                                )
                            } else {
                                drawPath(
                                    color = currentPathProperty.value.color,
                                    path = currentPath.value,
                                    style = style,
                                    alpha = currentPathProperty.value.color.alpha
                                )
                            }
                        }
                        restoreToCount(checkPoint)
                    }
                }
            },
            captureController = captureController
        )
    }
}
