package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.LocationException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.api.GeocodingResult
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.api.LocationApiService
import java.io.IOException

interface LocationRemoteDataSource {
    suspend fun findCitiesByQuery(query: String): List<City>
    suspend fun findCityByCoordinates(coordinates: City.Coordinates): City
}

class LocationRemoteDataSourceImpl(private val locationApiService: LocationApiService) : LocationRemoteDataSource {
    override suspend fun findCitiesByQuery(query: String): List<City> {
        try {
            val response = locationApiService.findCityByName(query = query)

            if (response.isSuccessful) {
                return response.body()!!.map { it.toModel() }
            } else {
                throw LocationException("Geocoding API responded with ${response.message()}.")
            }
        } catch (ex: IOException) {
            throw LocationException("Could not reach geocoding API.", ex)
        }
    }

    override suspend fun findCityByCoordinates(coordinates: City.Coordinates): City {
        try {
            val response = locationApiService.findCityByCoordinates(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
            )

            if (response.isSuccessful) {
                val results = response.body()!!.map { it.toModel() }

                if (results.isEmpty()) {
                    throw LocationException("Could not geocode location (${coordinates.latitude}, ${coordinates.longitude}).")
                }

                return results[0]
            } else {
                throw LocationException("Geocoding API responded with ${response.message()}.")
            }
        } catch (ex: IOException) {
            throw LocationException("Could not reach geocoding API.", ex)
        }
    }
}

private fun GeocodingResult.toModel(): City = City(
    coordinates = City.Coordinates(
        latitude = latitude,
        longitude = longitude,
    ),
    name = LocalizedName(
        arabic = cityName?.arabic,
        english = cityName?.english ?: cityNameEnglish,
    ),
    country = City.Country.byCode(countryCode),
)