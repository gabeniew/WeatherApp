package com.gabrielniewielski.weatherapp.ui.fragments.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.gabrielniewielski.weatherapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load preferences from XML
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Get Dark Mode preference
        val darkModePreference: SwitchPreferenceCompat? = findPreference("dark_mode")
        darkModePreference?.setOnPreferenceChangeListener { _, newValue ->
            val darkModeEnabled = newValue as Boolean
            toggleDarkMode(darkModeEnabled)
            true
        }

        // Get Temperature unit preference (Celsius/Fahrenheit)
        val temperatureUnitPreference: SwitchPreferenceCompat? = findPreference("temperature_unit")
        temperatureUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
            val isCelsius = newValue as Boolean
            updateTemperatureUnit(isCelsius)
            true
        }
    }

    private fun toggleDarkMode(enabled: Boolean) {
        if (enabled) {
            // Set dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Set light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateTemperatureUnit(isCelsius: Boolean) {
        // Save the selected temperature unit in SharedPreferences
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences?.edit()?.putBoolean("temperature_unit", isCelsius)?.apply()
    }
}