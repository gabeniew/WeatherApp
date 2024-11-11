package com.gabrielniewielski.weatherapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gabrielniewielski.weatherapp.data.models.Repository
import com.gabrielniewielski.weatherapp.data.models.database.ForecastEntity
import com.gabrielniewielski.weatherapp.data.models.database.WeatherEntity
import com.gabrielniewielski.weatherapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _currentWeather = MutableLiveData<WeatherEntity?>()
    val currentWeather: LiveData<WeatherEntity?> get() = _currentWeather

    private val _fiveDayForecast = MutableLiveData<List<ForecastEntity>?>()
    val fiveDayForecast: LiveData<List<ForecastEntity>?> get() = _fiveDayForecast

    private val _networkState = MutableLiveData<NetworkResult<String>>()
    val networkState: LiveData<NetworkResult<String>> get() = _networkState

    fun getCurrentWeather(cityName: String, apiKey: String, units: String) {
        _networkState.value = NetworkResult.Loading()
        viewModelScope.launch {
            if (hasInternetConnection()) {
                repository.getCurrentWeather(cityName, apiKey, units) { weather, error ->
                    if (weather != null) {
                        _currentWeather.value = weather
                        _networkState.value = NetworkResult.Success("Data loaded")
                    } else {
                        _networkState.value = NetworkResult.Error(message = error?.message ?: "Error fetching weather")
                    }
                }
            } else {
                val localWeather = repository.getCurrentWeatherFromDB(cityName)
                if (localWeather != null) {
                    _currentWeather.value = localWeather
                    _networkState.value = NetworkResult.Success("Loaded from cache")
                } else {
                    _networkState.value = NetworkResult.Error(message = "No internet and no cached data available")
                }
            }
        }
    }

    fun getFiveDayForecast(cityName: String, apiKey: String) {
        _networkState.value = NetworkResult.Loading()
        viewModelScope.launch {
            if (hasInternetConnection()) {
                repository.getFiveDayForecast(cityName, apiKey) { forecast, error ->
                    if (forecast != null) {
                        _fiveDayForecast.value = forecast
                        _networkState.value = NetworkResult.Success("Forecast loaded")
                    } else {
                        _networkState.value = NetworkResult.Error(message = error?.message ?: "Error fetching forecast")
                    }
                }
            } else {
                val localForecast = repository.getFiveDayForecastFromDB(cityName)
                if (!localForecast.isNullOrEmpty()) {
                    _fiveDayForecast.value = localForecast
                    _networkState.value = NetworkResult.Success("Loaded from cache")
                } else {
                    _networkState.value = NetworkResult.Error(message = "No internet and no cached data available")
                }
            }
        }
    }

    fun getCurrentWeatherByLocation(refresh:Boolean, latitude: Double, longitude: Double, apiKey: String, units: String) {
        _networkState.value = NetworkResult.Loading()
        viewModelScope.launch {
            if (hasInternetConnection() || refresh) {
                repository.getCurrentWeatherByLocation(latitude, longitude, apiKey, units) { weather, error ->
                    if (weather != null) {
                        _currentWeather.value = weather
                        _networkState.value = NetworkResult.Success("Data loaded")
                    } else {
                        _networkState.value = NetworkResult.Error(message = error?.message ?: "Error fetching weather")
                    }
                }
            } else {
                val localWeather = repository.getCurrentWeatherByLocation(latitude, longitude)
                if (localWeather != null) {
                    _currentWeather.value = localWeather
                    _networkState.value = NetworkResult.Success("Loaded from cache")
                } else {
                    _networkState.value = NetworkResult.Error(message = "No internet and no cached data available")
                }
            }
        }
    }

    fun getFiveDayForecastByLocation(refresh:Boolean,latitude: Double, longitude: Double, apiKey: String, units: String) {
        _networkState.value = NetworkResult.Loading()
        viewModelScope.launch {
            if (hasInternetConnection() || refresh) {
                repository.getFiveDayForecastByLocation(latitude, longitude, apiKey, units) { forecast, error ->
                    if (forecast != null) {
                        _fiveDayForecast.value = forecast
                        _networkState.value = NetworkResult.Success("Forecast loaded")
                    } else {
                        _networkState.value = NetworkResult.Error(message = error?.message ?: "Error fetching forecast")
                    }
                }
            } else {
                val localForecast = repository.getFiveDayForecastByLocationFromDB(latitude, longitude)
                if (!localForecast.isNullOrEmpty()) {
                    _fiveDayForecast.value = localForecast
                    _networkState.value = NetworkResult.Success("Loaded from cache")
                } else {
                    _networkState.value = NetworkResult.Error(message = "No internet and no cached data available")
                }
            }
        }
    }

   private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}