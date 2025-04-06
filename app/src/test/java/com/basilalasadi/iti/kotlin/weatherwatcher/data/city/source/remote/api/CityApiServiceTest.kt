package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api

import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CityApiServiceTest {
    private val service = CityApiService.create()

    @Test
    fun findCityByName_cairoArabic_cairo() = runTest {
        val response = service.findCityByName("القاهرة")
        println(response)

        assert(response.isSuccessful)

        val results = response.body()!!
        println(response.body())

        assert(results.isNotEmpty() && results.fastAny { it.cityName?.arabic == "القاهرة" && it.countryCode == "EG" })

        return@runTest
    }

    @Test
    fun findCityByName_cairoEnglish_successfulResponse() = runTest {
        val response = service.findCityByName("cairo")
        println(response)

        assert(response.isSuccessful)

        val results = response.body()!!
        println(response.body())

        assert(results.isNotEmpty() && results.fastAny { it.cityName?.english == "Cairo" && it.countryCode == "EG" })

        return@runTest
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
