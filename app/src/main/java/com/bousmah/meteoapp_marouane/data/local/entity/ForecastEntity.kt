package com.bousmah.meteoapp_marouane.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String,
    val description: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
