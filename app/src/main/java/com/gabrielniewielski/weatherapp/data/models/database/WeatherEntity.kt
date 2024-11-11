package com.gabrielniewielski.weatherapp.data.models.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gabrielniewielski.weatherapp.utils.Constants.Companion.WEATHER_TABLE

@Entity(tableName = WEATHER_TABLE)
data class WeatherEntity(
    @PrimaryKey val cityName: String, // Using city name as the primary key
    val latitude: Double,  // Latitude of the location
    val longitude: Double, // Longitude of the location
    val temperature: Double,
    val temperatureUnits: String,
    val weatherCondition: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val humidity: Int,
    val timestamp: Long // Stores when this data was last updated
)