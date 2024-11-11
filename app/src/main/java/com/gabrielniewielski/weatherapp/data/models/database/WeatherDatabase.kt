package com.gabrielniewielski.weatherapp.data.models.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [WeatherEntity::class, ForecastEntity::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDAO
}