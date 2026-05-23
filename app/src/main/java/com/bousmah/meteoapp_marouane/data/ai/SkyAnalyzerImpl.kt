package com.bousmah.meteoapp_marouane.data.ai

import android.graphics.Bitmap
import com.bousmah.meteoapp_marouane.domain.ai.SkyAnalyzer
import com.bousmah.meteoapp_marouane.domain.model.SkyCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkyAnalyzerImpl @Inject constructor() : SkyAnalyzer {

    override suspend fun analyze(bitmap: Bitmap): SkyCondition = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height

        var totalBrightness = 0.0
        var totalRed = 0.0
        var totalGreen = 0.0
        var totalBlue = 0.0
        var sampleCount = 0
        var darkPixelCount = 0
        var grayPixelCount = 0

        val step = 15
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height step step) {
            for (x in 0 until width step step) {
                val pixel = pixels[y * width + x]
                val r = android.graphics.Color.red(pixel)
                val g = android.graphics.Color.green(pixel)
                val b = android.graphics.Color.blue(pixel)

                val brightness = 0.299 * r + 0.587 * g + 0.114 * b
                totalBrightness += brightness
                totalRed += r
                totalGreen += g
                totalBlue += b
                sampleCount++

                if (brightness < 60) darkPixelCount++

                val maxC = maxOf(r, g, b)
                val minC = minOf(r, g, b)
                if (maxC - minC < 30) grayPixelCount++
            }
        }

        val avgBrightness = totalBrightness / sampleCount
        val avgR = totalRed / sampleCount
        val avgG = totalGreen / sampleCount
        val avgB = totalBlue / sampleCount
        val darkRatio = darkPixelCount.toDouble() / sampleCount
        val grayRatio = grayPixelCount.toDouble() / sampleCount

        return@withContext when {
            avgBrightness < 55 -> SkyCondition.NIGHT
            avgBrightness < 120 && darkRatio > 0.35 && grayRatio > 0.5 -> SkyCondition.RAINY
            grayRatio > 0.7 && avgBrightness in 100.0..190.0 -> SkyCondition.FOGGY
            grayRatio > 0.5 && avgBrightness < 170 -> SkyCondition.CLOUDY
            avgBrightness > 170 && avgB > avgR && avgB > avgG && avgB > 120 -> SkyCondition.SUNNY
            avgBrightness > 170 -> SkyCondition.SUNNY
            avgBrightness > 120 -> SkyCondition.CLOUDY
            else -> SkyCondition.CLOUDY
        }
    }
}
