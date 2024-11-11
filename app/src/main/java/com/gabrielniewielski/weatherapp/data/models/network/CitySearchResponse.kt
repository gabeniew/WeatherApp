package com.gabrielniewielski.weatherapp.data.models.network

data class CitySearchResponse(
    val list: List<SearchedCity>
)

data class SearchedCity(
    val name: String,
    val state: String?,
    val country: String,
    val lat: Double,
    val lon: Double
)