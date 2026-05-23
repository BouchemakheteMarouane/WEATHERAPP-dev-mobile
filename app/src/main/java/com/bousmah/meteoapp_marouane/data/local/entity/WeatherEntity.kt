package com.bousmah.meteoapp_marouane.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val cityName: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val uvIndex: Double,
    val feelsLike: Double,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
