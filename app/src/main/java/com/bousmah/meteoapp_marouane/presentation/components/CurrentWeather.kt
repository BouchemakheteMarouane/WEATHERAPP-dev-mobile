package com.bousmah.meteoapp_marouane.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bousmah.meteoapp_marouane.domain.model.Weather

@Composable
fun CurrentWeather(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(weather.cityName) { visible = true }

    // Breathing animation on temperature
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 3 }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // City name
            Text(
                text = weather.cityName.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(4.dp))

            // Date/time would go here in a real app
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(16.dp))

            // Weather icon
            WeatherAnimation(
                iconCode = weather.icon,
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
            )

            Spacer(Modifier.height(8.dp))

            // Big temperature
            Text(
                text = "${weather.temperature.toInt()}°",
                fontSize = 96.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                lineHeight = 96.sp
            )

            // Feels like
            Text(
                text = "Feels like ${weather.feelsLike.toInt()}°C",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.55f)
            )
        }
    }
}
