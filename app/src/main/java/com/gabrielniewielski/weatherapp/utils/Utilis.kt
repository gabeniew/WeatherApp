package com.gabrielniewielski.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000) // Convert to milliseconds
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Choose your format
    return format.format(date)
}

fun convertDateToTimestamp(dateString: String): Long {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Match the format used in formatDate()
    val date = format.parse(dateString)
    return date?.time ?: 0L
}

fun convertTemperature(temperature: Double, fromUnit: String, toUnit: String): Double {
    return when (fromUnit to toUnit) {
        "metric" to "imperial" -> temperature * 9 / 5 + 32 // Celsius to Fahrenheit
        "imperial" to "metric" -> (temperature - 32) * 5 / 9 // Fahrenheit to Celsius
        else -> temperature // If the units are the same, no conversion needed
    }
}
