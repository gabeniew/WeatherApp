package com.gabrielniewielski.weatherapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gabrielniewielski.weatherapp.R
import com.gabrielniewielski.weatherapp.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment(R.layout.fragment_weather_details) {

    private lateinit var binding: FragmentWeatherDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: retrieve data passed from previous fragment
        val sunriseTime = arguments?.getString("sunriseTime") ?: "N/A"
        val sunsetTime = arguments?.getString("sunsetTime") ?: "N/A"
        val description = arguments?.getString("description") ?: "N/A"
        val pressure = arguments?.getInt("pressure") ?: 0
        val visibility = arguments?.getInt("visibility") ?: 0

        // Populate UI with the received data
        binding.sunriseTimeText.text = sunriseTime
        binding.sunsetTimeText.text = sunsetTime
        binding.weatherDescriptionText.text = description
        binding.pressureText.text = "$pressure hPa"
        binding.visibilityText.text = "${visibility} m"
    }
}