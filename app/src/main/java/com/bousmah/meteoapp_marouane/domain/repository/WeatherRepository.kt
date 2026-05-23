package com.bousmah.meteoapp_marouane.domain.repository

import com.bousmah.meteoapp_marouane.domain.model.Forecast
import com.bousmah.meteoapp_marouane.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Result<Weather>
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<Weather>
    suspend fun get7DayForecast(lat: Double, lon: Double): Result<List<Forecast>>
}
