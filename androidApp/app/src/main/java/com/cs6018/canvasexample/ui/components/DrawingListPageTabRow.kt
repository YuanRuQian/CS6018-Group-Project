package com.cs6018.canvasexample.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.cs6018.canvasexample.network.ApiViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawingListPageTabRow(
    apiViewModel: ApiViewModel,
    currentActiveTabIndex: Int,
    updateCurrentActiveTabIndex: (Int) -> Unit,
    navigateToCanvasPage: () -> Unit,
    navigateToAuthenticationScreen: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val tabInfo = listOf(
        TabInfo("History", Icons.Filled.History) {
            scope.launch {
                delay(500)
                apiViewModel.getCurrentUserDrawingHistory()
            }
        },
        TabInfo("Explore", Icons.Filled.Explore) {
            scope.launch {
                delay(500)
                apiViewModel.getCurrentUserExploreFeed()
            }
        },
        TabInfo("Create", Icons.Filled.Add) {
            apiViewModel.setActiveDrawingBackgroundImageReference(null)
            navigateToCanvasPage()
        },
        // TODO: add sign out
        TabInfo("Sign Out", Icons.Filled.ExitToApp) {
            // TODO: first pop up a dialog to confirm sign out then call signOut
            scope.launch {
                Firebase.auth.signOut()
                delay(500)
                apiViewModel.resetData()
                navigateToAuthenticationScreen()
            }
        }
    )

    TabRow(selectedTabIndex = currentActiveTabIndex) {
        tabInfo.forEachIndexed { index, tab ->
            Tab(
                selected = currentActiveTabIndex == index,
                onClick = {
                    updateCurrentActiveTabIndex(index)
                    tab.onClick()
                },
                icon = {
                    Icon(
                        tab.icon, "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = { Text(text = tab.text, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

class TabInfo(val text: String, val icon: ImageVector, val onClick: () -> Unit = {})
