package com.cs6018.canvasexample.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.cs6018.canvasexample.R
import com.cs6018.canvasexample.data.PathPropertiesViewModel
import com.cs6018.canvasexample.data.ShakeDetectionViewModel

@Composable
fun BaseAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    iconResource: Int,
    showDialog: Boolean
) {

    if (showDialog) {
        AlertDialog(
            icon = {
                Icon(painter = painterResource(id = iconResource), contentDescription = dialogTitle)
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

}

@Composable
fun UndoAlertDialog(
    pathPropertiesViewModel: PathPropertiesViewModel,
    shakeDetectionViewModel: ShakeDetectionViewModel
) {

    BaseAlertDialog(
        { shakeDetectionViewModel.setAsNoShake() },
        {
            shakeDetectionViewModel.setAsNoShake()
            pathPropertiesViewModel.undoLastAction()
        },
        "Light Shake to Undo",
        "Are you sure you want to undo last path?",
        R.drawable.undo,
        shakeDetectionViewModel.isLightShake.value
    )
}


@Composable
fun ClearAllAlertDialog(
    pathPropertiesViewModel: PathPropertiesViewModel,
    shakeDetectionViewModel: ShakeDetectionViewModel
) {
    BaseAlertDialog(
        { shakeDetectionViewModel.setAsNoShake() },
        {
            shakeDetectionViewModel.setAsNoShake()
            // TODO: Implement the clear all method for path properties view model
            pathPropertiesViewModel.undoLastAction()
        },
        "Hard Shake to Clear All",
        "Are you sure you want to clear all current changes?",
        // TODO: Change icon resource to a clear all icon
        R.drawable.undo,
        shakeDetectionViewModel.isHardShake.value
    )

}



