package com.bousmah.meteoapp_zayd.util

object Sanitizer {
    /**
     * Sanitizes strings from API responses to prevent XSS or injection if displayed.
     * Trims and removes any HTML tags or suspicious characters.
     * Preserves apostrophes so city names like "Coeur d'Alene" display correctly.
     */
    fun sanitize(input: String?): String {
        if (input == null) return "N/A"
        return input.trim()
            .replace(Regex("<[^>]*>"), "") // Remove HTML tags
            .replace(Regex("[\\\\\"]"), "") // Remove backslashes and double quotes only
    }

    /**
     * Sanitizes numeric values to ensure they are within reasonable bounds.
     */
    fun sanitizeTemp(temp: Double): Double {
        return temp.coerceIn(-100.0, 100.0)
    }
}