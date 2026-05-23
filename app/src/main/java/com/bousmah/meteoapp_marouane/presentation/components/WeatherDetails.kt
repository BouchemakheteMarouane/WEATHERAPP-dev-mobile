package com.bousmah.meteoapp_marouane.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bousmah.meteoapp_marouane.domain.model.Weather

@Composable
fun WeatherDetails(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 2 }
    ) {
        Column(
            modifier = modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Details",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.WaterDrop,
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    iconTint = Color(0xFF64B5F6)
                )
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Air,
                    label = "Wind",
                    value = "${weather.windSpeed.toInt()} m/s",
                    iconTint = Color(0xFF80CBC4)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.WbSunny,
                    label = "UV Index",
                    value = uvLabel(weather.uvIndex),
                    iconTint = Color(0xFFFFD54F)
                )
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Thermostat,
                    label = "Feels Like",
                    value = "${weather.feelsLike.toInt()}°C",
                    iconTint = Color(0xFFEF9A9A)
                )
            }
        }
    }
}

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(16.dp))
                Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun uvLabel(uv: Double): String = when {
    uv < 3 -> "Low"
    uv < 6 -> "Moderate"
    uv < 8 -> "High"
    uv < 11 -> "Very High"
    else -> "Extreme"
}
