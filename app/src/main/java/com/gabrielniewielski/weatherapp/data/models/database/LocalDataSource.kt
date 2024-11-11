package com.gabrielniewielski.weatherapp.data.models.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val weatherDao: WeatherDAO) {

    // Fetch current weather data from the database based on city name
    fun getCurrentWeather(cityName: String): Flow<WeatherEntity> {
        return weatherDao.getCurrentWeather(cityName)
    }

    // Fetch current weather data from the database based on latitude and longitude
    fun getCurrentWeatherByLocation(latitude: Double, longitude: Double): Flow<WeatherEntity?> {
        return weatherDao.getCurrentWeatherByLocation(latitude, longitude)
    }

    // Fetch forecast data from the database based on city name
    fun getFiveDayForecast(cityName: String): Flow<List<ForecastEntity>> {
        return weatherDao.getForecast(cityName)
    }

    // Fetch forecast data from the database based on latitude and longitude
    fun getFiveDayForecastByLocation(latitude: Double, longitude: Double): Flow<List<ForecastEntity>> {
        return weatherDao.getForecastByLocation(latitude, longitude)
    }

    // Insert current weather data into the database
    fun saveCurrentWeather(weather: WeatherEntity) {
        weatherDao.insertCurrentWeather(weather)
    }

    // Insert forecast data into the database
    fun saveForecast(forecastList: List<ForecastEntity>) {
        weatherDao.insertForecast(forecastList)
    }

    fun isWeatherDataAvailable(): Boolean {
        return weatherDao.isWeatherDataAvailable() > 0
    }

    fun isForecastDataAvailable(): Boolean {
        return weatherDao.isForecastDataAvailable() > 0
    }
}