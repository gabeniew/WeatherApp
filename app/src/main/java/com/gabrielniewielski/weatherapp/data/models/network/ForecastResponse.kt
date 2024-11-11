package com.gabrielniewielski.weatherapp.data.models.network

data class ForecastResponse(
    val city: City, // City information (name, coordinates, country)
    val list: List<ForecastItem> // List of forecast data (one per interval)
)

data class City(
    val name: String, // City name
    val country: String, // Country code (e.g., "US")
    val coord: Coord // Coordinates (latitude and longitude)
)

data class Coord(
    val lat: Double,  // Latitude
    val lon: Double   // Longitude
)

data class ForecastItem(
    val dt: Long, // Date and time of the forecasted data (in Unix timestamp format)
    val main: MainForecast, // Temperature data for the forecast
    val weather: List<Weather>, // Weather condition(s) for this forecast interval
    val wind: Wind, // Wind speed for this forecast interval
    val sys: Sys // Sunrise and sunset information
)

data class MainForecast(
    val temp: Double, // Temperature
    val temp_min: Double, // Minimum temperature for the day
    val temp_max: Double, // Maximum temperature for the day
    val pressure: Int, // Atmospheric pressure
    val humidity: Int // Humidity percentage
)

data class Sys(
    val sunrise: Long, // Sunrise time (Unix timestamp)
    val sunset: Long   // Sunset time (Unix timestamp)
)
