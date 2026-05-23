package com.bousmah.meteoapp_marouane.data.remote

import com.bousmah.meteoapp_marouane.data.remote.dto.ForecastDto
import com.bousmah.meteoapp_marouane.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto

    @GET("forecast")
    suspend fun get7DayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastDto

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}
