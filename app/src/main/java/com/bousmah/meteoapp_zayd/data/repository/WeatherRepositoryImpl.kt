package com.bousmah.meteoapp_zayd.data.repository

import com.bousmah.meteoapp_zayd.BuildConfig
import com.bousmah.meteoapp_zayd.data.remote.WeatherApi
import com.bousmah.meteoapp_zayd.domain.model.Forecast
import com.bousmah.meteoapp_zayd.domain.model.Weather
import com.bousmah.meteoapp_zayd.domain.repository.WeatherRepository
import com.bousmah.meteoapp_zayd.util.Sanitizer
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> {
        return try {
            val dto = api.getWeatherByCity(city, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
            Result.success(Weather(
                temperature = Sanitizer.sanitizeTemp(dto.main.temp),
                description = Sanitizer.sanitize(dto.weather.firstOrNull()?.description ?: "N/A"),
                icon = Sanitizer.sanitize(dto.weather.firstOrNull()?.icon ?: ""),
                humidity = dto.main.humidity,
                windSpeed = dto.wind.speed,
                uvIndex = 0.0,
                feelsLike = Sanitizer.sanitizeTemp(dto.main.feelsLike),
                cityName = Sanitizer.sanitize(dto.name),
                latitude = dto.coord.lat,
                longitude = dto.coord.lon
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<Weather> {
        return try {
            val dto = api.getWeatherByLocation(lat, lon, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
            Result.success(Weather(
                temperature = Sanitizer.sanitizeTemp(dto.main.temp),
                description = Sanitizer.sanitize(dto.weather.firstOrNull()?.description ?: "N/A"),
                icon = Sanitizer.sanitize(dto.weather.firstOrNull()?.icon ?: ""),
                humidity = dto.main.humidity,
                windSpeed = dto.wind.speed,
                uvIndex = 0.0,
                feelsLike = Sanitizer.sanitizeTemp(dto.main.feelsLike),
                cityName = Sanitizer.sanitize(dto.name),
                latitude = dto.coord.lat,
                longitude = dto.coord.lon
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun get7DayForecast(lat: Double, lon: Double): Result<List<Forecast>> {
        return try {
            val dto = api.get7DayForecast(lat, lon, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
            val forecasts = dto.list.filter { it.dtTxt.contains("12:00:00") }.map {
                Forecast(
                    date = Sanitizer.sanitize(it.dtTxt.split(" ")[0]),
                    minTemp = Sanitizer.sanitizeTemp(it.main.temp),
                    maxTemp = Sanitizer.sanitizeTemp(it.main.temp),
                    icon = Sanitizer.sanitize(it.weather.firstOrNull()?.icon ?: ""),
                    description = Sanitizer.sanitize(it.weather.firstOrNull()?.description ?: "")
                )
            }
            Result.success(forecasts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
