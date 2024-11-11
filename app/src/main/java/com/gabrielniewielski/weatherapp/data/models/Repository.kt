package com.gabrielniewielski.weatherapp.data.models

import com.gabrielniewielski.weatherapp.data.models.database.ForecastEntity
import com.gabrielniewielski.weatherapp.data.models.database.LocalDataSource
import com.gabrielniewielski.weatherapp.data.models.database.WeatherEntity
import com.gabrielniewielski.weatherapp.data.models.network.NetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) {

    suspend fun getCurrentWeatherFromDB(cityName: String): WeatherEntity? {
        return localDataSource.getCurrentWeather(cityName).firstOrNull()
    }

    suspend fun getCurrentWeatherByLocation(latitude: Double, longitude: Double): WeatherEntity? {
        return localDataSource.getCurrentWeatherByLocation(latitude, longitude).firstOrNull()
    }

    suspend fun getFiveDayForecastFromDB(cityName: String): List<ForecastEntity>? {
        return localDataSource.getFiveDayForecast(cityName).firstOrNull()
    }

    suspend fun getFiveDayForecastByLocationFromDB(latitude: Double, longitude: Double): List<ForecastEntity>? {
        return localDataSource.getFiveDayForecastByLocation(latitude, longitude).firstOrNull()
    }

    // Fetch the current weather by city name
   suspend fun getCurrentWeather(
        cityName: String,
        apiKey: String,
        units: String,
        callback: (WeatherEntity?, Throwable?) -> Unit
    ) {
        // Check for data in the local database first
        val localWeather = getCurrentWeatherFromDB(cityName)

        if (localWeather != null && localWeather.temperatureUnits == units) {
            // If valid data is available locally, return it
            callback(localWeather, null)
        } else {
            // Otherwise, fetch from the network
            networkDataSource.fetchCurrentWeather(cityName, apiKey, units) { networkWeather, error ->
                if (networkWeather != null) {
                    // Map the network response to the WeatherEntity and store it locally
                    val weatherEntity = WeatherEntity(
                        cityName = networkWeather.name,
                        latitude = networkWeather.coord.lat,
                        longitude = networkWeather.coord.lon,
                        temperature = networkWeather.main.temp,
                        temperatureUnits = units,
                        weatherCondition = networkWeather.weather[0].main,
                        weatherDescription = networkWeather.weather[0].description,
                        weatherIcon = networkWeather.weather[0].icon,
                        windSpeed = networkWeather.wind.speed,
                        humidity = networkWeather.main.humidity,
                        timestamp = System.currentTimeMillis()
                    )
                    saveWeatherData(weatherEntity)
                    callback(weatherEntity, null)
                } else {
                    callback(null, error)
                }
            }
        }
    }

    // Fetch current weather by location (latitude, longitude)
   suspend fun getCurrentWeatherByLocation(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String,
        callback: (WeatherEntity?, Throwable?) -> Unit
    ) {
        val localWeather = getCurrentWeatherByLocation(latitude,longitude)

        if (localWeather != null && localWeather.temperatureUnits == units) {
            callback(localWeather, null)
        } else {
            networkDataSource.fetchCurrentWeatherByLocation(latitude, longitude, apiKey, units) { networkWeather, error ->
                if (networkWeather != null) {
                    val weatherEntity = WeatherEntity(
                        cityName = networkWeather.name,
                        latitude = latitude,
                        longitude = longitude,
                        temperature = networkWeather.main.temp,
                        temperatureUnits = units,
                        weatherCondition = networkWeather.weather[0].main,
                        weatherDescription = networkWeather.weather[0].description,
                        weatherIcon = networkWeather.weather[0].icon,
                        windSpeed = networkWeather.wind.speed,
                        humidity = networkWeather.main.humidity,
                        timestamp = System.currentTimeMillis()
                    )
                    saveWeatherData(weatherEntity)
                    callback(weatherEntity, null)
                } else {
                    callback(null, error)
                }
            }
        }
    }

    // Fetch the 5-day weather forecast for a city name
   suspend fun getFiveDayForecast(
        cityName: String,
        apiKey: String,
        callback: (List<ForecastEntity>?, Throwable?) -> Unit
    ) {
        val localForecast = getFiveDayForecastFromDB(cityName)

        if (!localForecast.isNullOrEmpty()) {
            callback(localForecast, null)
        } else {
            networkDataSource.fetchFiveDayForecast(cityName, apiKey) { networkForecast, error ->
                if (networkForecast != null) {
                    val forecastList = networkForecast.list.map {
                        ForecastEntity(
                            cityName = cityName,
                            latitude = networkForecast.city.coord.lat,
                            longitude = networkForecast.city.coord.lon,
                            date = it.dt,
                            tempMin = it.main.temp_min,
                            tempMax = it.main.temp_max,
                            weatherCondition = it.weather[0].main,
                            weatherDescription = it.weather[0].description,
                            weatherIcon = it.weather[0].icon,
                            windSpeed = it.wind.speed,
                            humidity = it.main.humidity,
                            sunrise = it.sys.sunrise,
                            sunset = it.sys.sunset
                        )
                    }
                    saveForecastData(forecastList)
                    callback(forecastList, null)
                } else {
                    callback(null, error)
                }
            }
        }
    }

    // Fetch the 5-day weather forecast by location (latitude, longitude)
    suspend fun getFiveDayForecastByLocation(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String,
        callback: (List<ForecastEntity>?, Throwable?) -> Unit
    ) {
        val localForecast = getFiveDayForecastByLocationFromDB(latitude,longitude)

        if (!localForecast.isNullOrEmpty()) {
            callback(localForecast, null)
        } else {
            networkDataSource.fetchFiveDayForecastByLocation(latitude, longitude, apiKey, units) { networkForecast, error ->
                if (networkForecast != null) {
                    val forecastList = networkForecast.list.map {
                        ForecastEntity(
                            cityName = networkForecast.city.name,
                            latitude = latitude,
                            longitude = longitude,
                            date = it.dt,
                            tempMin = it.main.temp_min,
                            tempMax = it.main.temp_max,
                            weatherCondition = it.weather[0].main,
                            weatherDescription = it.weather[0].description,
                            weatherIcon = it.weather[0].icon,
                            windSpeed = it.wind.speed,
                            humidity = it.main.humidity,
                            sunrise = it.sys.sunrise,
                            sunset = it.sys.sunset
                        )
                    }
                    saveForecastData(forecastList)
                    callback(forecastList, null)
                } else {
                    callback(null, error)
                }
            }
        }
    }

    // Helper functions to save data locally
    private fun saveWeatherData(weatherEntity: WeatherEntity) {
        // Save to local database asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.saveCurrentWeather(weatherEntity)
        }
    }

    private fun saveForecastData(forecastList: List<ForecastEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.saveForecast(forecastList)
        }
    }

    suspend fun isDBDataAvailable(): Boolean {
        // Perform database operations inside withContext to offload to IO dispatcher
        return withContext(Dispatchers.IO) {
            val isWeatherDataAvailable = localDataSource.isWeatherDataAvailable()
            val isForecastDataAvailable = localDataSource.isForecastDataAvailable()
            isWeatherDataAvailable || isForecastDataAvailable
        }
    }
}