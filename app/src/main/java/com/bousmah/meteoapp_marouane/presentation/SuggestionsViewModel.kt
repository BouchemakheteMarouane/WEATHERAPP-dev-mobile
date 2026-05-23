package com.bousmah.meteoapp_marouane.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bousmah.meteoapp_marouane.data.remote.ChatApi
import com.bousmah.meteoapp_marouane.data.remote.OllamaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuggestionSection(
    val emoji: String,
    val title: String,
    val content: String
)

data class SuggestionsUiState(
    val city: String = "",
    val temperature: String = "",
    val conditions: String = "",
    val humidity: String = "",
    val wind: String = "",
    val uv: String = "",
    val isLoading: Boolean = false,
    val sections: List<SuggestionSection> = emptyList(),
    val rawResponse: String = "",
    val error: String? = null
)

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val chatApi: ChatApi
) : ViewModel() {

    private val _state = MutableStateFlow(SuggestionsUiState())
    val state = _state.asStateFlow()

    private var weatherData = ""

    fun initWeatherData(
        city: String,
        temp: Double,
        description: String,
        humidity: Int,
        windSpeed: Double,
        uvIndex: Double
    ) {
        weatherData = "$city, ${temp.toInt()}°C, $description, humidity $humidity%, wind ${windSpeed}m/s, UV $uvIndex"
        _state.update { it.copy(
            city = city,
            temperature = "${temp.toInt()}°C",
            conditions = description,
            humidity = "$humidity%",
            wind = "${windSpeed}m/s",
            uv = "$uvIndex"
        ) }
    }

    fun generateSuggestions() {
        if (weatherData.isBlank()) return

        _state.update { it.copy(isLoading = true, error = null, sections = emptyList(), rawResponse = "") }

        viewModelScope.launch {
            try {
                val prompt = buildPrompt()
                val response = chatApi.generate(OllamaRequest(
                    model = "llama3.2:3b",
                    prompt = prompt,
                    stream = false
                ))
                val sections = parseSections(response.response)
                _state.update { it.copy(
                    sections = sections,
                    rawResponse = response.response,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to get suggestions. Make sure Ollama is running."
                ) }
            }
        }
    }

    private fun buildPrompt(): String {
        return """Given this weather: $weatherData.
Give me: outfit suggestions, activity ideas, food recommendations, and health tips. Be concise and friendly.

Format your response with these exact section headers (include the emoji):
👕 WHAT TO WEAR:
[your outfit advice here]

🏃 ACTIVITIES:
[your activity ideas here]

🍽️ FOOD & DRINKS:
[your food recommendations here]

💡 HEALTH TIPS:
[your health tips here]"""
    }

    private fun parseSections(raw: String): List<SuggestionSection> {
        val sections = mutableListOf<SuggestionSection>()
        val headerMap = linkedMapOf(
            "👕" to "What to Wear",
            "🏃" to "Activities",
            "🍽️" to "Food & Drinks",
            "💡" to "Health Tips"
        )

        val lines = raw.split("\n")
        var currentEmoji = ""
        var currentTitle = ""
        val currentContent = StringBuilder()

        for (line in lines) {
            val matched = headerMap.entries.find { (emoji, _) -> line.contains(emoji) }
            if (matched != null) {
                if (currentEmoji.isNotEmpty()) {
                    sections.add(SuggestionSection(currentEmoji, currentTitle, currentContent.toString().trim()))
                }
                currentEmoji = matched.key
                currentTitle = matched.value
                currentContent.clear()
                val afterHeader = line.substringAfter(matched.key).removePrefix(":").trim()
                if (afterHeader.isNotEmpty()) {
                    currentContent.appendLine(afterHeader)
                }
            } else if (currentEmoji.isNotEmpty()) {
                val trimmed = line.trim()
                if (trimmed.isNotEmpty()) {
                    currentContent.appendLine(trimmed)
                }
            }
        }

        if (currentEmoji.isNotEmpty() && currentContent.isNotBlank()) {
            sections.add(SuggestionSection(currentEmoji, currentTitle, currentContent.toString().trim()))
        }

        if (sections.isEmpty() && raw.isNotBlank()) {
            sections.add(SuggestionSection("💡", "Suggestions", raw.trim()))
        }

        return sections
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
