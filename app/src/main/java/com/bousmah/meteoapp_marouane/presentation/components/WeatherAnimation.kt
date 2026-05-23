package com.bousmah.meteoapp_marouane.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*

@Composable
fun WeatherAnimation(
    iconCode: String,
    modifier: Modifier = Modifier
) {
    // Mapping OpenWeather codes to Lottie animations (Remote URLs for demonstration)
    val animationUrl = when (iconCode) {
        "01d" -> "https://assets9.lottiefiles.com/temp/lf20_dgj83P.json" // Clear sky (day)
        "01n" -> "https://assets9.lottiefiles.com/temp/lf20_y79S89.json" // Clear sky (night)
        "02d", "02n", "03d", "03n", "04d", "04n" -> "https://assets9.lottiefiles.com/temp/lf20_V9t630.json" // Cloudy
        "09d", "09n", "10d", "10n" -> "https://assets9.lottiefiles.com/temp/lf20_rpZ66W.json" // Rain
        "11d", "11n" -> "https://assets9.lottiefiles.com/temp/lf20_Xk1482.json" // Thunderstorm
        "13d", "13n" -> "https://assets9.lottiefiles.com/temp/lf20_BS9ulx.json" // Snow
        "50d", "50n" -> "https://assets9.lottiefiles.com/temp/lf20_kOfSST.json" // Mist
        else -> "https://assets9.lottiefiles.com/temp/lf20_dgj83P.json"
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Url(animationUrl))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
