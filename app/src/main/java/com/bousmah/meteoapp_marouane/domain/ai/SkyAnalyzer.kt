package com.bousmah.meteoapp_marouane.domain.ai

import android.graphics.Bitmap
import com.bousmah.meteoapp_marouane.domain.model.SkyCondition

interface SkyAnalyzer {
    suspend fun analyze(bitmap: Bitmap): SkyCondition
}
