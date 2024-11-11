package com.gabrielniewielski.weatherapp.utils

class Constants {
    companion object {
        const val API_KEY = "52d7f269f0dd461f40662df77be940f4"
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        // Query params consts
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        // Room
        const val DATABASE_NAME = "weather_db"
        const val WEATHER_TABLE = "weather_table2"
        const val FORECAST_TABLE = "forecast_table"
    }
}