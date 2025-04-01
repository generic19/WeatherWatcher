package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.CityException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api.GeocodingResult
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api.CityApiService
import java.io.IOException

interface CityRemoteDataSource {
    suspend fun findCitiesByQuery(query: String): List<City>
    suspend fun findCityByCoordinates(coordinates: City.Coordinates): City
}

class CityRemoteDataSourceImpl(private val cityApiService: CityApiService) : CityRemoteDataSource {
    override suspend fun findCitiesByQuery(query: String): List<City> {
        try {
            val response = cityApiService.findCityByName(query = query)

            if (response.isSuccessful) {
                return response.body()!!.map { it.toModel() }
            } else {
                throw CityException("Geocoding API responded with ${response.message()}.")
            }
        } catch (ex: IOException) {
            throw CityException("Could not reach geocoding API.", ex)
        }
    }

    override suspend fun findCityByCoordinates(coordinates: City.Coordinates): City {
        try {
            val response = cityApiService.findCityByCoordinates(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
            )

            if (response.isSuccessful) {
                val results = response.body()!!.map { it.toModel() }

                if (results.isEmpty()) {
                    throw CityException("Could not geocode location (${coordinates.latitude}, ${coordinates.longitude}).")
                }

                return results[0]
            } else {
                throw CityException("Geocoding API responded with ${response.message()}.")
            }
        } catch (ex: IOException) {
            throw CityException("Could not reach geocoding API.", ex)
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