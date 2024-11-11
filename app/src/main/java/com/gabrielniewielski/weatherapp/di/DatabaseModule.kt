package com.gabrielniewielski.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.gabrielniewielski.weatherapp.data.models.database.WeatherDatabase
import com.gabrielniewielski.weatherapp.utils.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        WeatherDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRecipesDAO(database: WeatherDatabase) = database.weatherDao()
}