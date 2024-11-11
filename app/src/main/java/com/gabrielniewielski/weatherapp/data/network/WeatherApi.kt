package com.gabrielniewielski.weatherapp.data.network


import com.gabrielniewielski.weatherapp.data.models.network.ForecastResponse
import com.gabrielniewielski.weatherapp.data.models.network.SearchedCity
import com.gabrielniewielski.weatherapp.data.models.network.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Fetch current weather data using city name
    @GET("weather")
    fun getCurrentWeather(@Query("q") city: String,
                                  @Query("appid") apiKey: String,
                                  @Query("units") units: String
    ): Call<WeatherResponse>

    // Fetch current weather data using latitude and longitude
    @GET("weather")
    fun getCurrentWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String // Celsius or Fahrenheit
    ): Call<WeatherResponse>

    // Fetch 5-day weather forecast data using city name
    @GET("forecast")
    fun getFiveDayForecast(@Query("q") city: String,
                                   @Query("appid") apiKey: String): Call<ForecastResponse>

    // Fetch 5-day weather forecast data using latitude and longitude
    @GET("forecast")
    fun getFiveDayForecastByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String // Celsius or Fahrenheit
    ): Call<ForecastResponse>

    @GET("geo/2.5/direct")
    fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Call<List<SearchedCity>>
}