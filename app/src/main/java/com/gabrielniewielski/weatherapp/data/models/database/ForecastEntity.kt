package com.gabrielniewielski.weatherapp.data.models.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gabrielniewielski.weatherapp.utils.Constants.Companion.FORECAST_TABLE

@Entity(tableName = FORECAST_TABLE)
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String, // Links forecast data to a specific city
    val latitude: Double,  // Latitude of the location
    val longitude: Double, // Longitude of the location
    val date: Long, // Date of forecast (stored as Unix timestamp)
    val tempMin: Double,
    val tempMax: Double,
    val weatherCondition: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val humidity: Int,
    val sunrise: Long, // Sunrise time for the day
    val sunset: Long // Sunset time for the day
)