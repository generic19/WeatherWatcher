package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import kotlinx.coroutines.runBlocking
import org.junit.Test

class WeatherRemoteDataSourceTest {
    private val remote = WeatherRemoteDataSourceImpl(WeatherApiService.create())

    private val city = City(
        coordinates = City.Coordinates(
            latitude = 30.0444,
            longitude = 31.2357,
        ),
        name = LocalizedName("القاهرة", "Cairo"),
        country = City.Country.EG,
    )

    @Test
    fun getCurrentWeather_cairoAndArabic_noExceptions() = runBlocking {
        val weather = remote.getCurrentWeather(city, AppLocale.Arabic)
        println(weather)

        return@runBlocking
    }

    @Test
    fun getWeatherForecast_cairoAndArabic_noExceptions() = runBlocking {
        val weather = remote.getWeatherForecast(city, AppLocale.Arabic)
        println(weather)

        return@runBlocking
    }
}
