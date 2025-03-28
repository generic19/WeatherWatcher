package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.api.LocationApiService
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LocationRemoteDataSourceTest {
    private val remote = LocationRemoteDataSourceImpl(LocationApiService.create())

    private val city = City(
        coordinates = City.Coordinates(
            latitude = 30.0444,
            longitude = 31.2357,
        ),
        name = LocalizedName("القاهرة", "Cairo"),
        country = City.Country.EG,
    )

    @Test
    fun findCitiesByQuery_cairo_cairoCity() = runTest {
        val results = remote.findCitiesByQuery("cairo")
        assert(results.any { it.name.get(AppLocale.English).equals("Cairo", ignoreCase = true) })
    }

    @Test
    fun findCitiesByQuery_nonexistentCity_emptyList() = runTest {
        val results = remote.findCitiesByQuery("aaaabbbbbccccccdddddeeeeeeefffffffff")
        assert(results.isEmpty())
    }

    @Test
    fun findCityByCoordinates_cairo_cairo() = runTest {
        val actual = remote.findCityByCoordinates(city.coordinates)
        assert(actual.name.get(AppLocale.English).equals("Cairo", ignoreCase = true))
    }
}