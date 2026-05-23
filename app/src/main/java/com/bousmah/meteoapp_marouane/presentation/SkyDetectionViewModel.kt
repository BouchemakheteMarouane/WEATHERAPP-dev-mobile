package com.bousmah.meteoapp_marouane.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bousmah.meteoapp_marouane.domain.ai.SkyAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkyDetectionViewModel @Inject constructor(
    private val skyAnalyzer: SkyAnalyzer
) : ViewModel() {

    private val _state = MutableStateFlow(SkyDetectionState())
    val state = _state.asStateFlow()

    fun analyze(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null) }
            try {
                val result = skyAnalyzer.analyze(bitmap)
                _state.update { it.copy(condition = result, isAnalyzing = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = "Analysis failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun analyzeError(message: String) {
        _state.update { it.copy(error = message) }
    }

    fun reset() {
        _state.update { SkyDetectionState() }
    }
}
