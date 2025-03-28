package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api

import kotlinx.coroutines.runBlocking
import org.junit.Test

class WeatherApiServiceTest {
    private val service = WeatherApiService.create()

    @Test
    fun getCurrentWeather_coordinatesAndArabic_successfulResponse() = runBlocking {
        val response = service.getCurrentWeather(30.0444, 31.2357, "ar")
        println(response)

        assert(response.isSuccessful)
        println(response.body())

        return@runBlocking
    }

    @Test
    fun getCurrentWeather_coordinatesAndEnglish_successfulResponse() = runBlocking {
        val response = service.getCurrentWeather(30.0444, 31.2357, "en")
        println(response)

        assert(response.isSuccessful)
        println(response.body())

        return@runBlocking
    }
}