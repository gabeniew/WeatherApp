package com.gabrielniewielski.weatherapp.ui.fragments.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.gabrielniewielski.weatherapp.R
import com.gabrielniewielski.weatherapp.adapters.ForecastAdapter
import com.gabrielniewielski.weatherapp.data.models.database.WeatherEntity
import com.gabrielniewielski.weatherapp.databinding.FragmentHomeBinding
import com.gabrielniewielski.weatherapp.utils.Constants.Companion.API_KEY
import com.gabrielniewielski.weatherapp.utils.formatDate
import com.gabrielniewielski.weatherapp.viewmodels.HomeViewModel
import com.gabrielniewielski.weatherapp.viewmodels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: MainViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var forecastAdapter: ForecastAdapter

    private var searchJob: Job? = null

    // For handling location permissions and retrieving location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            fetchLocationAndWeather(refresh = false)
        } else {
            showPermissionDeniedDialog()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Directly set the Toolbar as the ActionBar for this Fragment
        val toolbar: Toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setHasOptionsMenu(true)  // Enable fragment to handle options menu

        // Setup UI elements
        setupUI()
        registerObservers()
    }

    private fun setupUI() {
        setupForecastAdapter()
        setupSearchEdit()
    }

    private fun setupForecastAdapter() {
        forecastAdapter = ForecastAdapter() { forecast ->

            // Navigate to DetailsFragment, passing the forecast data
//            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
//                date = forecast.date,
//                tempMin = forecast.tempMin,
//                tempMax = forecast.tempMax,
//                weatherCondition = forecast.weatherCondition
//                // Add more parameters if needed
//            )
        }
        binding.forecastRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.forecastRecyclerView.adapter = forecastAdapter
    }

    private fun setupSearchEdit() {
        // Set up search listener for city search
        binding.searchEditText.addTextChangedListener { text ->
            searchJob?.cancel() // Cancel any previous job if a new character is typed
            searchJob = lifecycleScope.launch {
                delay(1000) // Wait 2 seconds after user stops typing
                text?.let {
                    if (it.isNotEmpty()) {
                        fetchWeatherForCity(it.toString(),currentUnits())
                        binding.searchEditText.text?.clear() // Clear the EditText
                    }
                }
            }
        }
        binding.searchEditText.setOnClickListener {
            val searchText = binding.searchEditText.text.toString()
            if (searchText.isNotEmpty()) {
                fetchWeatherForCity(searchText, currentUnits())
                binding.searchEditText.text?.clear() // Clear the EditText
            }
        }
    }

    private fun registerObservers() {
        // Observe the visibility of CardView and RecyclerView
        homeViewModel.cardViewVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.currentWeatherLayout.visibility = visibility
        }

        homeViewModel.recyclerViewVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.forecastRecyclerView.visibility = visibility
        }

        // Observe the location permission status
        homeViewModel.locationPermissionStatus.observe(viewLifecycleOwner) { permissionGranted ->
            if (permissionGranted) {
                fetchLocationAndWeather(refresh = false)
            } else {
                showPermissionDeniedDialog()
            }
        }

        // Observe current weather data
        viewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            currentWeather?.let {
                // Update UI with current weather data
                updateCurrentWeatherUI(it)
                homeViewModel.updateUIVisibility(true) // Show UI when data is available
            }
        }

        // Observe 5-day forecast data
        viewModel.fiveDayForecast.observe(viewLifecycleOwner) { forecastEntities ->
            forecastEntities?.let {
                // Map ForecastEntity to Forecast
                val forecastList = it.map { entity ->
                    Forecast(
                        date = formatDate(entity.date),  // Format the date as needed
                        tempMin = entity.tempMin,
                        tempMax = entity.tempMax,
                        weatherCondition = entity.weatherCondition
                    )
                }
                forecastAdapter.submitList(forecastList)
                homeViewModel.updateUIVisibility(true) // Show UI when data is available
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Setup location services and permission
        setupLocation()
    }

    private fun setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    // Toolbar setup
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu_items, menu)  // Assuming home_menu.xml contains search and settings icons
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                toggleSearchEditTextVisibility()  // Show/hide search EditText
                return true
            }
            R.id.action_refresh-> {
               fetchLocationAndWeather(refresh = true)
                return true
            }
            R.id.action_settings -> {
                // Open Settings Drawer or navigate to Settings Fragment
                openSettingsDrawer()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun toggleSearchEditTextVisibility() {
        if (binding.searchTextInputLayout.visibility == View.GONE) {
            binding.searchTextInputLayout.visibility = View.VISIBLE
            binding.searchTextInputLayout.requestFocus()
        } else {
            binding.searchTextInputLayout.visibility = View.GONE
        }
    }

    private fun openSettingsDrawer() {
        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
    }


    // Weather Fetching calls
    private fun fetchWeatherForCity(cityName: String,units: String) {
        viewModel.getCurrentWeather(cityName, API_KEY,units)  // Pass your API key here
        viewModel.getFiveDayForecast(cityName, API_KEY)
    }

    private fun fetchWeatherForLocation(refresh: Boolean,latitude: Double, longitude: Double) {
        viewModel.getCurrentWeatherByLocation(refresh,latitude, longitude, API_KEY,currentUnits())
        viewModel.getFiveDayForecastByLocation(refresh,latitude, longitude, API_KEY,currentUnits())
    }

    private fun fetchLocationAndWeather(refresh: Boolean) {
        // Check if location permission is granted before attempting to access location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    fetchWeatherForLocation(refresh, it.latitude, it.longitude)
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // UI helper methods
    private fun updateCurrentWeatherUI(currentWeather: WeatherEntity) {
        val temperatureSuffix = if (isCelsius()) "°C" else "°F"
        binding.currentCity.text = "${currentWeather.cityName}"
        binding.currentTemperature.text = "${currentWeather.temperature}$temperatureSuffix"
        loadWeatherIcon(currentWeather.weatherIcon)
        binding.currentWeatherCondition.text = currentWeather.weatherCondition
        binding.currentWindSpeed.text = "Wind: ${currentWeather.windSpeed} km/h"
        binding.currentHumidity.text = "Humidity: ${currentWeather.humidity}%"
    }

    private fun loadWeatherIcon(iconCode: String) {
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        binding.currentWeatherIcon.load(iconUrl) {
            crossfade(true)
        }
    }

    private fun currentUnits(): String {
        return if (isCelsius()) "metric" else "imperial"
    }

    private fun isCelsius(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isCelsius = sharedPreferences.getBoolean("temperature_unit", true)
        return isCelsius
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location Permission Required")
            .setMessage("This app requires location permission to fetch weather data for your location. Please enable it in Settings.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}