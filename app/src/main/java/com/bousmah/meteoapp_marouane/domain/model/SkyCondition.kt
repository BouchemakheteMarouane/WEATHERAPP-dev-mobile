package com.bousmah.meteoapp_marouane.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Umbrella
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class SkyCondition(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    SUNNY(
        label = "Sunny",
        icon = Icons.Rounded.WbSunny,
        color = Color(0xFFFFD93D)
    ),
    CLOUDY(
        label = "Cloudy",
        icon = Icons.Rounded.Cloud,
        color = Color(0xFF9E9E9E)
    ),
    RAINY(
        label = "Rainy",
        icon = Icons.Rounded.Umbrella,
        color = Color(0xFF4FC3F7)
    ),
    FOGGY(
        label = "Foggy",
        icon = Icons.Rounded.Air,
        color = Color(0xFFB0BEC5)
    ),
    NIGHT(
        label = "Night",
        icon = Icons.Rounded.DarkMode,
        color = Color(0xFF1A237E)
    ),
    UNKNOWN(
        label = "Unknown",
        icon = Icons.Rounded.HelpOutline,
        color = Color(0xFF757575)
    )
}
