package com.gabrielniewielski.weatherapp.ui.fragments.home

data class Forecast(
    val date: String,             // Forecast date (e.g., "2024-11-09")
    val tempMin: Double,          // Minimum temperature for the day
    val tempMax: Double,          // Maximum temperature for the day
    val weatherCondition: String  // Description of the weather (e.g., "Sunny")
)
