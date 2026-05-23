package com.bousmah.meteoapp_marouane.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bousmah.meteoapp_marouane.domain.location.LocationTracker
import com.bousmah.meteoapp_marouane.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    fun loadWeatherInfo(city: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, weather = null) }
            
            if (city != null) {
                // OWASP: Input Validation
                if (!validateCityName(city)) {
                    _state.update { it.copy(isLoading = false, error = "Invalid city name. Use only letters, spaces, and hyphens.", weather = null) }
                    return@launch
                }
                fetchWeatherByCity(city)
            } else {
                fetchWeatherByLocation()
            }
        }
    }

    private fun validateCityName(city: String): Boolean {
        // OWASP: Allow letters (including accented), spaces, hyphens, and apostrophes
        val regex = "^[\\p{L}\\s\\-']{1,100}$".toRegex()
        return regex.matches(city)
    }

    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600) // Debounce search to avoid excessive API calls
            if (query.isNotBlank() && query.length >= 2) {
                loadWeatherInfo(query)
            }
        }
    }

    private suspend fun fetchWeatherByLocation() {
        locationTracker.getCurrentLocation()?.let { location ->
            updateWeatherAndForecast(location.latitude, location.longitude)
        } ?: run {
            // Fallback to default city if GPS unavailable (emulator friendly)
            fetchWeatherByCity("Casablanca")
        }
    }

    private suspend fun fetchWeatherByCity(city: String) {
        val weatherResult = repository.getWeatherByCity(city)
        weatherResult.onSuccess { weather ->
            val forecastResult = repository.get7DayForecast(weather.latitude, weather.longitude)
            _state.update { it.copy(
                weather = weather,
                forecast = forecastResult.getOrDefault(emptyList()),
                isLoading = false,
                error = null
            ) }
        }.onFailure {
            _state.update { it.copy(weather = null, isLoading = false, error = "City not found or network error.") }
        }
    }

    private suspend fun updateWeatherAndForecast(lat: Double, lon: Double) {
        val weatherResult = repository.getWeatherByLocation(lat, lon)
        val forecastResult = repository.get7DayForecast(lat, lon)
        
        _state.update { it.copy(
            weather = weatherResult.getOrNull(),
            forecast = forecastResult.getOrDefault(emptyList()),
            isLoading = false,
            error = if (weatherResult.isFailure) "Network error. Please check your connection." else null
        ) }
    }
}
