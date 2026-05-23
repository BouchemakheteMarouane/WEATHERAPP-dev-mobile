package com.bousmah.meteoapp_marouane.domain.model

data class Weather(
    val temperature: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val uvIndex: Double,
    val feelsLike: Double,
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)

data class Forecast(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String,
    val description: String
)
