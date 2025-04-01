package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.WeatherLocalDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getLatestWeatherFlow(city: City): Flow<Dated<Weather>?>
    fun getWeatherForecastFlow(city: City): Flow<List<Dated<Weather>>>
    
    suspend fun loadCurrentWeather(city: City)
    suspend fun loadWeatherForecast(city: City)
}

class WeatherRepositoryImpl(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {

    override fun getLatestWeatherFlow(city: City): Flow<Dated<Weather>?> =
        localDataSource.getLatestWeatherFlow(city)
    
    override suspend fun loadCurrentWeather(city: City) {
        val remoteWeather = remoteDataSource.getCurrentWeather(city).toDatedLocated(city)
        localDataSource.putWeather(listOf(remoteWeather))
    }
    
    override fun getWeatherForecastFlow(city: City): Flow<List<Dated<Weather>>> =
        localDataSource.getWeatherForecastFlow(city)
    
    override suspend fun loadWeatherForecast(city: City) {
        val remoteWeathers = remoteDataSource.getWeatherForecast(city).map { it.toDatedLocated(city) }
        localDataSource.putWeather(remoteWeathers)
    }
}
