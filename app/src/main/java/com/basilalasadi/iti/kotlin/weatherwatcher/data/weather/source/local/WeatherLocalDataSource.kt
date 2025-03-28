package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local

import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getLatestWeatherFlow(city: City): Flow<Weather>
    fun getHourlyWeatherFlow(city: City): Flow<List<Dated<Weather>>>
    fun getDailyWeatherFlow(city: City): Flow<List<Dated<Weather>>>
    suspend fun putWeather(city:City, vararg weather: Dated<Weather>)
}

