package com.bousmah.meteoapp_marouane.presentation

import com.bousmah.meteoapp_marouane.domain.model.Forecast
import com.bousmah.meteoapp_marouane.domain.model.Weather

data class WeatherState(
    val weather: Weather? = null,
    val forecast: List<Forecast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
