package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local

import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.DatedLocated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.WeatherDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.dto.toEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.dto.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Period
import java.time.ZonedDateTime

interface WeatherLocalDataSource {
    fun getPresentWeatherFlow(city: City): Flow<Dated<Weather>?>
    fun getWeatherForecastFlow(city: City): Flow<List<Dated<Weather>>>
    suspend fun putWeather(weathers: List<DatedLocated<Weather>>)
}

class WeatherLocalDataSourceImpl(private val weatherDao: WeatherDao) : WeatherLocalDataSource {
    override fun getPresentWeatherFlow(city: City): Flow<Dated<Weather>?> =
        weatherDao.getPresentWeatherFlow(
            cityName = city.name.get(AppLocale.English),
            cityCountry = city.country,
            now = ZonedDateTime.now(),
        ).map {
            it?.toModel()?.run { Dated(dateTime, value) }
        }
    
    override fun getWeatherForecastFlow(city: City): Flow<List<Dated<Weather>>> {
        val start = ZonedDateTime.now()
        val end = start + Period.ofDays(5)
        
        return weatherDao.getWeatherRange(
            cityName = city.name.get(AppLocale.English),
            cityCountry = city.country,
            start = start,
            end = end,
        ).map { it.map { it.toModel().toDated() } }
    }
    
    override suspend fun putWeather(weathers: List<DatedLocated<Weather>>) {
        weatherDao.putWeather(weathers.map { it.toEntity() })
    }
}
