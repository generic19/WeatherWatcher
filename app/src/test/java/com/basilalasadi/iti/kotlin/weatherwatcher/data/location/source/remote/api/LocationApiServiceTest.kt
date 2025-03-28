package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.api

import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LocationApiServiceTest {
    private val service = LocationApiService.create()

    @Test
    fun findCityByName_cairoArabic_cairo() = runBlocking {
        val response = service.findCityByName("القاهرة")
        println(response)

        assert(response.isSuccessful)

        val results = response.body()!!
        println(response.body())

        assert(results.isNotEmpty() && results.fastAny { it.cityName?.arabic == "القاهرة" && it.countryCode == "EG" })

        return@runBlocking
    }

    @Test
    fun findCityByName_cairoEnglish_successfulResponse() = runBlocking {
        val response = service.findCityByName("cairo")
        println(response)

        assert(response.isSuccessful)

        val results = response.body()!!
        println(response.body())

        assert(results.isNotEmpty() && results.fastAny { it.cityName?.english == "Cairo" && it.countryCode == "EG" })

        return@runBlocking
    }

    @Test
    fun findCityByCoordinates_cairoCoordinates_cairo() = runBlocking {
        val response = service.findCityByCoordinates(30.0444, 31.2357)
        println(response)

        assert(response.isSuccessful)

        val results = response.body()!!
        println(response.body())

        assert(results.isNotEmpty() && results[0].let { it.cityName?.english == "Cairo" && it.countryCode == "EG" })

        return@runBlocking
    }
}
