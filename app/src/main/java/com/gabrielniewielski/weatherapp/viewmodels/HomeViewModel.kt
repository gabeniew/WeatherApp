package com.gabrielniewielski.weatherapp.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gabrielniewielski.weatherapp.data.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application){

    private val _cardViewVisibility = MutableLiveData<Int>()
    val cardViewVisibility: LiveData<Int> get() = _cardViewVisibility

    private val _recyclerViewVisibility = MutableLiveData<Int>()
    val recyclerViewVisibility: LiveData<Int> get() = _recyclerViewVisibility

    private val _locationPermissionStatus = MutableLiveData<Boolean>()
    val locationPermissionStatus: LiveData<Boolean> get() = _locationPermissionStatus

    init {
        checkLocationPermission()
        checkInitialConditions()
    }

    private fun checkLocationPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            getApplication(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _locationPermissionStatus.value = permissionStatus
    }

    private fun checkInitialConditions() {
        viewModelScope.launch {
            val isDBDataAvailable = repository.isDBDataAvailable()

            // Set visibility of CardView and RecyclerView based on data availability
            if (!isDBDataAvailable) {
                _cardViewVisibility.value = View.GONE
                _recyclerViewVisibility.value = View.GONE
            } else {
                _cardViewVisibility.value = View.VISIBLE
                _recyclerViewVisibility.value = View.VISIBLE
            }
        }
    }

    fun updateUIVisibility(isDataAvailable: Boolean) {
        if (isDataAvailable) {
            _cardViewVisibility.value = View.VISIBLE
            _recyclerViewVisibility.value = View.VISIBLE
        } else {
            _cardViewVisibility.value = View.GONE
            _recyclerViewVisibility.value = View.GONE
        }
    }
}