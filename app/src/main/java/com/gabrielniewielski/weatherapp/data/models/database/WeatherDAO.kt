package com.gabrielniewielski.weatherapp.data.models.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
    // Insert current weather data, replacing old data for the same city or location
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(currentWeather: WeatherEntity)

    // Insert or update the forecast data (replace old data for each day)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecast(forecastList: List<ForecastEntity>)

    // Get the current weather for a specific city by city name
    @Query("SELECT * FROM weather_table2 WHERE cityName = :cityName LIMIT 1")
    fun getCurrentWeather(cityName: String): Flow<WeatherEntity>

    // Get the current weather for a specific location (latitude, longitude)
    @Query("SELECT * FROM weather_table2 WHERE latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun getCurrentWeatherByLocation(latitude: Double, longitude: Double): Flow<WeatherEntity?>

    // Get the 5-day forecast for a specific city by city name
    @Query("SELECT * FROM forecast_table WHERE cityName = :cityName ORDER BY date ASC LIMIT 5")
    fun getForecast(cityName: String): Flow<List<ForecastEntity>>

    // Get the 5-day forecast for a specific location (latitude, longitude)
    @Query("SELECT * FROM forecast_table WHERE latitude = :latitude AND longitude = :longitude ORDER BY date ASC LIMIT 5")
    fun getForecastByLocation(latitude: Double, longitude: Double): Flow<List<ForecastEntity>>

    // Clear old forecast data for a specific city (optional, for cache cleanup)
    @Query("DELETE FROM forecast_table WHERE cityName = :cityName")
    fun clearForecast(cityName: String): Int

    // Clear old forecast data for a specific location (latitude, longitude)
    @Query("DELETE FROM forecast_table WHERE latitude = :latitude AND longitude = :longitude")
    fun clearForecastByLocation(latitude: Double, longitude: Double): Int

    // Query to check if the table has any data
    @Query("SELECT COUNT(*) FROM weather_table2")
    fun isWeatherDataAvailable(): Int

    // Query to check if the table has any data
    @Query("SELECT COUNT(*) FROM forecast_table")
    fun isForecastDataAvailable(): Int
}