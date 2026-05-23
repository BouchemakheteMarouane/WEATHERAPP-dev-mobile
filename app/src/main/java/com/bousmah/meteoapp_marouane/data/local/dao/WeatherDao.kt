package com.bousmah.meteoapp_marouane.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bousmah.meteoapp_marouane.data.local.entity.ForecastEntity
import com.bousmah.meteoapp_marouane.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<ForecastEntity>)

    @Query("SELECT * FROM weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeather(cityName: String): WeatherEntity?

    @Query("SELECT * FROM weather ORDER BY lastUpdated DESC")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Query("SELECT * FROM forecast WHERE cityName = :cityName ORDER BY date ASC")
    suspend fun getForecasts(cityName: String): List<ForecastEntity>

    @Query("DELETE FROM weather WHERE cityName = :cityName")
    suspend fun deleteWeather(cityName: String)

    @Query("DELETE FROM forecast WHERE cityName = :cityName")
    suspend fun deleteForecasts(cityName: String)

    @Query("SELECT DISTINCT cityName FROM weather ORDER BY lastUpdated DESC LIMIT 10")
    suspend fun getRecentCities(): List<String>

    @Query("DELETE FROM forecast")
    suspend fun deleteAllForecasts()
}
