package com.gabrielniewielski.weatherapp.data.models.network

import com.gabrielniewielski.weatherapp.data.network.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val weatherApiService: WeatherApi) {

    // Fetch current weather data from the network based on city name
    fun fetchCurrentWeather(
        cityName: String,
        apiKey: String,
        units: String,
        callback: (WeatherResponse?, Throwable?) -> Unit
    ) {

        weatherApiService.getCurrentWeather(cityName, apiKey, units)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    NetworkResponseHandler.handleResponse(response, { data ->
                        callback(data, null)
                    }, { error ->
                        callback(null, error)
                    })
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    NetworkResponseHandler.handleFailure(t) { error -> callback(null, error) }
                }
            })
    }

    // Fetch current weather data from the network based on latitude and longitude
    fun fetchCurrentWeatherByLocation(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String,
        callback: (WeatherResponse?, Throwable?) -> Unit
    ) {
        weatherApiService.getCurrentWeatherByLocation(latitude, longitude, apiKey, units)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    NetworkResponseHandler.handleResponse(response, { data ->
                        callback(data, null)
                    }, { error ->
                        callback(null, error)
                    })
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    NetworkResponseHandler.handleFailure(t) { error -> callback(null, error) }
                }
            })
    }

    // Fetch the 5-day weather forecast data from the network based on city name
    fun fetchFiveDayForecast(
        cityName: String,
        apiKey: String,
        callback: (ForecastResponse?, Throwable?) -> Unit
    ) {
        weatherApiService.getFiveDayForecast(cityName, apiKey)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    NetworkResponseHandler.handleResponse(response, { data ->
                        callback(data, null)
                    }, { error ->
                        callback(null, error)
                    })
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    NetworkResponseHandler.handleFailure(t) { error -> callback(null, error) }
                }
            })
    }

    // Fetch the 5-day weather forecast data from the network based on latitude and longitude
    fun fetchFiveDayForecastByLocation(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String,
        callback: (ForecastResponse?, Throwable?) -> Unit
    ) {
        weatherApiService.getFiveDayForecastByLocation(latitude, longitude, apiKey, units)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    NetworkResponseHandler.handleResponse(response, { data ->
                        callback(data, null)
                    }, { error ->
                        callback(null, error)
                    })
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    NetworkResponseHandler.handleFailure(t) { error -> callback(null, error) }
                }
            })
    }
}
