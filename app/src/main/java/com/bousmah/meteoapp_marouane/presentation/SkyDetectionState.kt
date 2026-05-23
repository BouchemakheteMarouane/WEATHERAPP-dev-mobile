package com.bousmah.meteoapp_marouane.presentation

import com.bousmah.meteoapp_marouane.domain.model.SkyCondition

data class SkyDetectionState(
    val condition: SkyCondition? = null,
    val isAnalyzing: Boolean = false,
    val error: String? = null
)
