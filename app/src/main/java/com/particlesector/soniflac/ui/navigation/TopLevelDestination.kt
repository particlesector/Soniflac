package com.particlesector.soniflac.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val icon: ImageVector,
    val label: String,
    val route: String,
) {
    LIBRARY(
        icon = Icons.Filled.LibraryMusic,
        label = "Library",
        route = "library",
    ),
    RADIO(
        icon = Icons.Filled.Radio,
        label = "Radio",
        route = "radio",
    ),
    SETTINGS(
        icon = Icons.Filled.Settings,
        label = "Settings",
        route = "settings",
    ),
}
