package com.gabrielniewielski.weatherapp.data.models.network

data class WeatherResponse(
    val coord: Coord, // Coordinates (latitude and longitude)
    val main: Main, // Temperature and humidity information
    val weather: List<Weather>, // List of weather conditions (e.g., sunny, cloudy)
    val wind: Wind, // Wind speed information
    val name: String, // City name
)

data class Main(
    val temp: Double, // Current temperature
    val humidity: Int // Humidity percentage
)

data class Weather(
    val main: String, // Weather condition (e.g., "Clear", "Clouds")
    val description: String, // More detailed description (e.g., "clear sky")
    val icon: String // Icon ID for the weather condition (to load the image)
)

data class Wind(
    val speed: Double // Wind speed in m/s or km/h
)
