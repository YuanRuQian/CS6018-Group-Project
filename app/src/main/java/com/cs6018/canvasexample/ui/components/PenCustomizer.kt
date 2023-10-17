package com.cs6018.canvasexample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs6018.canvasexample.data.PathProperties
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun PenCustomizer(
    hexColorString: String,
    currentPathProperty: PathProperties,
    updateHexColorCode: (String) -> Unit,
    updateCurrentPathProperty: (newColor: Color?, newStrokeWidth: Float?, strokeCap: StrokeCap?, strokeJoin: StrokeJoin?) -> Unit,
    controller: ColorPickerController
) {

    var isInitialized by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 30.dp)
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = {
                // If don't do the check, the colorChanged method will be called first before the initial color is accepted by the color picker, which will override the passed in selected color
                // TODO: Find a better way to fix this
                if (!isInitialized) {
                    isInitialized = true
                } else {
                    updateHexColorCode(it.hexCode)
                    updateCurrentPathProperty(it.color, null, null, null)
                }
            },
            initialColor = currentPathProperty.color
        )

        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(30.dp),
            controller = controller,
            tileOddColor = Color.White,
            tileEvenColor = Color.Black
        )

        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(30.dp),
            controller = controller,
        )

        Text(
            text = "Adjust Stroke Width: ${currentPathProperty.strokeWidth} dp",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Add a Slider for choosing stroke width
        Slider(
            value = currentPathProperty.strokeWidth,
            onValueChange = {
                updateCurrentPathProperty(null, it, null, null)
            },
            valueRange = 1f..50f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        Text(
            text = "Hex Color: #$hexColorString",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = Color.Black
        )


        // Add a Selection Group for choosing StrokeCap
        Text(
            text = "Select Stroke Cap:",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(StrokeCap.Butt, StrokeCap.Round, StrokeCap.Square).forEach { cap ->
                val isSelected = currentPathProperty.strokeCap == cap
                val textColor = if (isSelected) Color.White else Color.LightGray
                Button(
                    onClick = { updateCurrentPathProperty(null, null, cap, null) },
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    Text(
                        text = cap.toString(),
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Add a Selection Group for choosing StrokeJoin
        Text(
            text = "Select Stroke Join:",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(StrokeJoin.Bevel, StrokeJoin.Miter, StrokeJoin.Round).forEach { join ->
                val isSelected = currentPathProperty.strokeJoin == join
                val textColor = if (isSelected) Color.White else Color.LightGray
                Button(
                    onClick = { updateCurrentPathProperty(null, null, null, join) },
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    Text(
                        text = join.toString(),
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }


            Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentPathProperty.strokeWidth.dp)
                    .clip(RoundedCornerShape(6.dp)),
                controller = controller,
                selectedColor = currentPathProperty.color
            )
        }
    }
}