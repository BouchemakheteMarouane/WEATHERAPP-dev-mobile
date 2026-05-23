package com.bousmah.meteoapp_marouane.data.repository

import com.bousmah.meteoapp_marouane.BuildConfig
import com.bousmah.meteoapp_marouane.data.local.dao.WeatherDao
import com.bousmah.meteoapp_marouane.data.local.entity.ForecastEntity
import com.bousmah.meteoapp_marouane.data.local.entity.WeatherEntity
import com.bousmah.meteoapp_marouane.data.remote.WeatherApi
import com.bousmah.meteoapp_marouane.data.remote.dto.WeatherDto
import com.bousmah.meteoapp_marouane.domain.model.Forecast
import com.bousmah.meteoapp_marouane.domain.model.Weather
import com.bousmah.meteoapp_marouane.domain.repository.WeatherRepository
import com.bousmah.meteoapp_marouane.util.Sanitizer
import retrofit2.HttpException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> {
        if (BuildConfig.OPEN_WEATHER_API_KEY == "UNSET") {
            return loadCachedWeather(city) ?: Result.failure(Exception("No cached data available"))
        }
        return try {
            val dto = api.getWeatherByCity(city, BuildConfig.OPEN_WEATHER_API_KEY)
            val weather = dto.toDomain()
            dao.insertWeather(weather.toEntity())
            Result.success(weather)
        } catch (e: Exception) {
            loadCachedWeather(city) ?: handleException(e)
        }
    }

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<Weather> {
        if (BuildConfig.OPEN_WEATHER_API_KEY == "UNSET") {
            return Result.failure(Exception("API Key is missing. Check local.properties"))
        }
        return try {
            val dto = api.getWeatherByLocation(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
            val weather = dto.toDomain()
            dao.insertWeather(weather.toEntity())
            Result.success(weather)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun get7DayForecast(lat: Double, lon: Double): Result<List<Forecast>> {
        if (BuildConfig.OPEN_WEATHER_API_KEY == "UNSET") {
            return Result.failure(Exception("API Key is missing. Check local.properties"))
        }
        return try {
            val dto = api.get7DayForecast(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
            val forecasts = dto.list.filter { it.dtTxt.contains("12:00:00") }.map {
                Forecast(
                    date = Sanitizer.sanitize(it.dtTxt.split(" ")[0]),
                    minTemp = Sanitizer.sanitizeTemp(it.main.tempMin),
                    maxTemp = Sanitizer.sanitizeTemp(it.main.tempMax),
                    icon = Sanitizer.sanitize(it.weather.firstOrNull()?.icon ?: ""),
                    description = Sanitizer.sanitize(it.weather.firstOrNull()?.description ?: "")
                )
            }
            dao.deleteAllForecasts()
            val cityWeather = dao.getAllWeather().firstOrNull()
            val cityName = cityWeather?.cityName ?: "Unknown"
            dao.insertForecasts(forecasts.map { it.toEntity(cityName) })
            Result.success(forecasts)
        } catch (e: Exception) {
            val cityWeather = dao.getAllWeather().firstOrNull()
            if (cityWeather != null) {
                val cached = dao.getForecasts(cityWeather.cityName)
                if (cached.isNotEmpty()) {
                    return Result.success(cached.map { it.toDomain() })
                }
            }
            handleException(e)
        }
    }

    suspend fun getCachedWeather(city: String): Weather? {
        return dao.getWeather(city)?.toDomain()
    }

    private suspend fun loadCachedWeather(city: String): Result<Weather>? {
        val cached = dao.getWeather(city)?.toDomain()
        return cached?.let { Result.success(it) }
    }

    private fun handleException(e: Exception): Result<Nothing> {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> Result.failure(Exception("Invalid API Key. Please check your OpenWeather account."))
                    404 -> Result.failure(Exception("City not found."))
                    else -> Result.failure(Exception("Network error: ${e.code()}"))
                }
            }
            else -> Result.failure(e)
        }
    }

    private fun WeatherDto.toDomain(): Weather {
        return Weather(
            temperature = Sanitizer.sanitizeTemp(main.temp),
            description = Sanitizer.sanitize(weather.firstOrNull()?.description ?: "N/A"),
            icon = Sanitizer.sanitize(weather.firstOrNull()?.icon ?: ""),
            humidity = main.humidity,
            windSpeed = wind.speed,
            uvIndex = 0.0,
            feelsLike = Sanitizer.sanitizeTemp(main.feelsLike),
            cityName = Sanitizer.sanitize(name),
            latitude = coord.lat,
            longitude = coord.lon
        )
    }

    private fun Weather.toEntity(): WeatherEntity {
        return WeatherEntity(
            cityName = cityName,
            temperature = temperature,
            description = description,
            icon = icon,
            humidity = humidity,
            windSpeed = windSpeed,
            uvIndex = uvIndex,
            feelsLike = feelsLike,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun WeatherEntity.toDomain(): Weather {
        return Weather(
            temperature = temperature,
            description = description,
            icon = icon,
            humidity = humidity,
            windSpeed = windSpeed,
            uvIndex = uvIndex,
            feelsLike = feelsLike,
            cityName = cityName,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun Forecast.toEntity(cityName: String): ForecastEntity {
        return ForecastEntity(
            cityName = cityName,
            date = date,
            minTemp = minTemp,
            maxTemp = maxTemp,
            icon = icon,
            description = description
        )
    }

    private fun ForecastEntity.toDomain(): Forecast {
        return Forecast(
            date = date,
            minTemp = minTemp,
            maxTemp = maxTemp,
            icon = icon,
            description = description
        )
    }
}
